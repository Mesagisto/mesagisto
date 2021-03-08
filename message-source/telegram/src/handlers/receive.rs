use std::{fmt::Write, ops::ControlFlow};

use arcstr::ArcStr;
use color_eyre::eyre::Result;
use futures_util::future::join_all;
use mesagisto_client::{
  data::{
    events::Event, message::{Message, MessageType}, Packet, Payload
  },
  db::DB,
  res::RES,
  server::SERVER,
   ResultExt,
};
use teloxide::{requests::Requester, types::ChatId, utils::html};
use tracing::trace;

use crate::{
  ext::{db::DbExt, WebpConverter},
  res::TG_RES,
  CONFIG, TG_BOT,
};

pub async fn recover() -> Result<()> {
  for pair in &CONFIG.bindings {
    let room_id = SERVER.room_id(pair.value().clone());
    SERVER
      .sub(room_id, &arcstr::literal!("mesagisto"))
      .await
      .log();
  }
  Ok(())
}

pub async fn add(room_address: &ArcStr) -> Result<()> {
  let room_id = SERVER.room_id(room_address.clone());
  SERVER
    .sub(room_id, &arcstr::literal!("mesagisto"))
    .await
    .log();
  Ok(())
}
pub async fn change(before_address: &ArcStr, after_address: &ArcStr) -> Result<()> {
  del(before_address).await?;
  add(after_address).await?;
  Ok(())
}
pub async fn del(room_address: &ArcStr) -> Result<()> {
  let room_id = SERVER.room_id(room_address.clone());
  // FIXME 同侧互通 考虑当接受到不属于任何群聊的数据包时才unsub
  // TODO 更新Config中的cache
  SERVER
    .unsub(&room_id, &arcstr::literal!("mesagisto"))
    .await
    .log();
  Ok(())
}

pub async fn packet_handler(pkt: Packet) -> Result<ControlFlow<Packet>> {
  tracing::debug!("recv msg pkt from {}", pkt.room_id);
  match pkt.decrypt() {
    Ok(Payload::MsgPayload(inner)) => {
      if let Some(targets) = CONFIG.target_id(pkt.room_id.clone()) {
        if targets.len() == 1 {
          let target = targets[0];
          if target.to_be_bytes() != *inner.from {
            msg_handler(inner, target, "mesagisto".into()).await?;
          }
        } else {
          let mut futs = Vec::new();
          for target in targets {
            if target.to_be_bytes() != *inner.from {
              futs.push(msg_handler(inner.clone(), target, "mesagisto".into()))
            }
          }
          join_all(futs).await;
        }
      };
    }
    Ok(Payload::EventPayload(Event::RequestImage { id })) if let Some(reply) = &pkt.reply => {
      let image_uid = id;
      let image_id = match DB.get_image_id(&image_uid) {
        Some(v) => v,
        None => return Ok(ControlFlow::Break(pkt)),
      };
      let file = String::from_utf8_lossy(&image_id);
      let file_path = TG_BOT.get_file(file).await.unwrap().path;
      let url = TG_BOT.get_url_by_path(file_path);
      let event = Event::RespondImage { id: image_uid, url };
      let packet = Packet::new(pkt.room_id, event.into())?;
      SERVER
        .respond(packet, reply.to_owned(), &arcstr::literal!("mesagisto"))
        .await?;
      return Ok(ControlFlow::Continue(()));
    }
    Ok(Payload::EventPayload(inner)) => {
      tracing::debug!("recv event pkt {:#?}", inner);
      return Ok(ControlFlow::Break(pkt));
    }
    Err(e) => {
      tracing::warn!(
        "未知的数据包类型，请更新本消息源，若已是最新请等待适配 {}",
        e
      );
    }
  }
  Ok(ControlFlow::Continue(()))
}

async fn msg_handler(mut message: Message, target: i64, server: ArcStr) -> Result<()> {
  let chat_id = ChatId(target);
  let room = CONFIG.room_address(&target).expect("Room不存在");
  let room_id = SERVER.room_id(room);

  let sender_name = if message.profile.nick.is_some() {
    message.profile.nick.take().unwrap()
  } else if message.profile.username.is_some() {
    message.profile.username.take().unwrap()
  } else {
    base64_url::encode(&message.profile.id)
  };

  let mut reunite_text = String::new();
  for single in message.chain {
    trace!(element = ?single,"正在处理消息链中的元素");
    match single {
      MessageType::Text { content } => {
        reunite_text.write_str(&html::escape(content.as_str()))?;
        reunite_text.write_str("\n")?;
      }
      MessageType::Image { id, url } => {
        let path = RES.file(&id, &url, &room_id, &server).await?;
        let receipt = TG_BOT
          .send_text(
            chat_id,
            format!("{}:", html::bold(sender_name.as_str())),
            None,
          )
          .await?;
        DB.put_msg_id_ir_2(&target, &receipt.id.0, &message.id)?;
        let receipt = if let Some(reply_to) = &message.reply {
          let local_id = DB.get_msg_id_1(&target, reply_to)?;
          TG_BOT.send_image(chat_id, &path, local_id).await?
        } else {
          TG_BOT.send_image(chat_id, &path, None).await?
        };
        DB.put_msg_id_1(&target, &message.id, &receipt.id.0)?;
      }
      MessageType::Sticker { id, url } => {
        RES.file(&id, &url, &room_id, &server).await?;
        let path = TG_RES
          .convert(
            &base64_url::encode(&id).into(),
            &"webp".into(),
            WebpConverter,
          )
          .await?;
        let receipt = TG_BOT
          .send_text(
            chat_id,
            format!("{}:", html::bold(sender_name.as_str())),
            None,
          )
          .await?;
        DB.put_msg_id_ir_2(&target, &receipt.id.0, &message.id)?;
        let receipt = if let Some(reply_to) = &message.reply {
          let local_id = DB.get_msg_id_1(&target, reply_to)?;
          TG_BOT.send_image(chat_id, &path, local_id).await?
        } else {
          TG_BOT.send_image(chat_id, &path, None).await?
        };
        DB.put_msg_id_1(&target, &message.id, &receipt.id.0)?;
      }
      MessageType::Edit { content: _ } => {}
      _ => {}
    }
  }
  if !reunite_text.is_empty() {
    let content = format!(
      "{}:\n{}",
      html::bold(sender_name.as_str()),
      html::escape(reunite_text.trim_end())
    );
    let receipt = if let Some(reply_to) = &message.reply {
      let local_id = DB.get_msg_id_1(&target, reply_to)?;
      TG_BOT.send_text(chat_id, content, local_id).await?
    } else {
      TG_BOT.send_text(chat_id, content, None).await?
    };
    DB.put_msg_id_1(&target, &message.id, &receipt.id.0)?;
  }

  Ok(())
}
