use std::ops::ControlFlow;

use arcstr::ArcStr;
use color_eyre::eyre::Result;
use futures_util::future::join_all;
use mesagisto_client::{
  res::RES,
  data::{
    events::Event,
    message::{Message, MessageType},
    Inbox, Packet,
  },
  db::DB,
  server::SERVER,
  EitherExt, ResultExt,
};
use serenity::{model::{
  channel::MessageReference,
  id::{ChannelId, MessageId},
}, builder::{CreateMessage, CreateAttachment}};
use tracing::trace;

use crate::{
  bot::{DcFile, BOT_CLIENT},
  config::CONFIG,
  ext::db::DbExt,
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
    .unsub(room_id, &arcstr::literal!("mesagisto"))
    .await
    .log();
  Ok(())
}

pub async fn packet_handler(pkt: Packet) -> Result<ControlFlow<Packet>> {
  tracing::debug!("recv msg pkt from {:#?}", pkt.room_id);
  match pkt.decrypt() {
    Ok(either::Either::Left(message)) => {
      if let Some(targets) = CONFIG.target_id(pkt.room_id.clone()) {
        if targets.len() == 1 {
          let target = targets[0];
          if target.to_be_bytes() != *message.from {
            msg_handler(message, target, "mesagisto".into()).await?;
          }
        } else {
          let mut futs = Vec::new();
          for target in targets {
            if target.to_be_bytes() != *message.from {
              futs.push(msg_handler(message.clone(), target, "mesagisto".into()))
            }
          }
          join_all(futs).await;
        }
      };
    }
    Ok(either::Either::Right(Event::RequestImage { id })) if pkt.inbox.is_some() => {
      let image_uid = id;
      if let Inbox::Request { id } = *pkt.inbox.clone().unwrap() {
        let image_id = match DB.get_image_id(&image_uid) {
          Some(v) => v,
          None => return Ok(ControlFlow::Break(pkt)),
        };
        let dc_file: DcFile = ciborium::de::from_reader(image_id.to_vec().as_slice())?;
        let url = dc_file.to_url();
        let event = Event::RespondImage { id: image_uid, url };
        let packet = Packet::new(pkt.room_id, event.to_right())?;
        SERVER
          .respond(packet, id, &arcstr::literal!("mesagisto"))
          .await?;
        return Ok(ControlFlow::Continue(()));
      } else {
        return Ok(ControlFlow::Break(pkt));
      }
    }
    Ok(either::Either::Right(event)) => {
      tracing::debug!("recv event pkt {:#?}", event);
      return Ok(ControlFlow::Break(pkt));
    }
    Err(e) => {
      tracing::warn!("未知的数据包类型，请更新本消息源，若已是最新请等待适配 {e}");
    }
  }
  Ok(ControlFlow::Continue(()))
}

async fn msg_handler(mut message: Message, target_id: u64, server: ArcStr) -> Result<()> {
  let target = BOT_CLIENT.get_channel(ChannelId::from(target_id)).await?.id();
  let room = CONFIG.room_address(&target_id).expect("Room不存在");
  let room_id = SERVER.room_id(room);

  let sender_name = if message.profile.nick.is_some() {
    message.profile.nick.take().unwrap()
  } else if message.profile.username.is_some() {
    message.profile.username.take().unwrap()
  } else {
    base64_url::encode(&message.profile.id)
  };

  for single in message.chain {
    trace!(element = ?single,"正在处理消息链中的元素");
    match single {
      MessageType::Text { content } => {
        let content = format!("{}: {}", sender_name, content);
        let receipt = if let Some(reply_to) = &message.reply {
          let local_id = DB.get_msg_id_1(&target_id, reply_to)?;
          match local_id {
            Some(local_id) => {
              let refer = MessageReference::from((ChannelId::from(target_id), MessageId::from(local_id)));
              target
                .send_message(&**BOT_CLIENT, CreateMessage::new().content(content).reference_message(refer))
                .await
            }
            None => {
              target
                .send_message(&**BOT_CLIENT, CreateMessage::new().content(content))
                .await
            }
          }
        } else {
          target
            .send_message(&**BOT_CLIENT, CreateMessage::new().content(content))
            .await
        }?;
        DB.put_msg_id_1(&target_id, &message.id, &receipt.id.get())?;
      }
      MessageType::Image { id, url } => {
        let path = RES.file(&id, &url, room_id.clone(), &server).await?;
        let receipt = target
          .send_message(&**BOT_CLIENT, CreateMessage::new().content(format!("{sender_name}:")))
          .await?;
        DB.put_msg_id_ir_2(&target_id, &receipt.id.get(), &message.id)?;
        let kind = infer::get_from_path(&path).expect("file read failed when refering file type");

        let filename = match kind {
          Some(ty) => format!("{:?}.{}", path.file_name().unwrap(), ty.extension()),
          None => path.file_name().unwrap().to_string_lossy().to_string(),
        };
        let attachment =  CreateAttachment::file(&tokio::fs::File::open(&path).await.unwrap(), filename).await?;

        let receipt = target
          .send_message(&**BOT_CLIENT, CreateMessage::new().add_file(attachment))
          .await?;
        DB.put_msg_id_1(&target_id, &message.id, &receipt.id.get())?;
      }
      MessageType::Sticker { id, url } => {
        let path = RES.file(&id, &url, room_id.clone(), &server).await?;
        let receipt = target
          .send_message(&**BOT_CLIENT, CreateMessage::new().content(format!("{sender_name}:")))
          .await?;
        DB.put_msg_id_ir_2(&target_id, &receipt.id.get(), &message.id)?;
        let kind = infer::get_from_path(&path).expect("file read failed when refering file type");

        let filename = match kind {
          Some(ty) => format!("{:?}.{}", path.file_name().unwrap(), ty.extension()),
          None => path.file_name().unwrap().to_string_lossy().to_string(),
        };
        let attachment =  CreateAttachment::file(&tokio::fs::File::open(&path).await.unwrap(), filename).await?;

        let receipt = target
          .send_message(&**BOT_CLIENT, CreateMessage::new().add_file(attachment))
          .await?;
        DB.put_msg_id_1(&target_id, &message.id, &receipt.id.get())?;
      }
      _ => {}
    }
  }

  Ok(())
}
