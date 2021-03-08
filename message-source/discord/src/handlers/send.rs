use arcstr::ArcStr;
use color_eyre::eyre::Result;
use mesagisto_client::{
  data::{
    message::{self, MessageType, Profile},
    Packet,
  },
  db::DB,
  res::RES,
  server::SERVER,
  EitherExt,
};
use serenity::model::channel::Message;

use crate::{
  bot::{DcFile, BOT_CLIENT},
  ext::{db::DbExt, res::ResExt},
  CONFIG,
};

pub async fn answer_common(msg: Message) -> Result<()> {
  let target = msg.channel_id.get();
  if !CONFIG.bindings.contains_key(&target.to_string()) {
    return Ok(());
  }
  let room_address = CONFIG.bindings.get(&target.to_string()).unwrap().clone();

  let sender = &msg.author;
  let nick = msg.member.as_ref().and_then(|v| v.nick.clone());
  let profile = Profile {
    id: sender.id.get().to_be_bytes().to_vec(), // fixme
    username: Some(sender.name.clone()),
    nick,
  };
  let mut chain = Vec::<MessageType>::new();
  if !msg.content.is_empty() {
    chain.push(MessageType::Text {
      content: msg.content.clone(),
    })
  }
  // fixme image group
  if let Some(attach) = msg.attachments.first() {
    match &attach.content_type {
      Some(ty) => {
        if ty.starts_with("image") {
          let dc_file = DcFile::new(
            &target,
            &attach.id.get(),
            &ArcStr::from(attach.filename.clone()),
          );
          RES.put_dc_image_id(&attach.id.get(), &dc_file)?;
          BOT_CLIENT.download_file(&dc_file).await?;
          chain.push(MessageType::Image {
            id: attach.id.get().to_be_bytes().to_vec(),
            url: None,
          });
        }
      }
      None => (),
    }
  }

  let reply = match &msg.referenced_message {
    Some(v) => {
      let local_id = v.id.get().to_be_bytes().to_vec();
      DB.get_msg_id_2(&target, &local_id).unwrap_or(None)
    }
    None => None,
  };
  // msg.attachments.get(0).unwrap().content_type;
  DB.put_msg_id_ir_0(&target, &msg.id.get(), &msg.id.get())?;
  let message = message::Message {
    profile,
    id: msg.id.get().to_be_bytes().to_vec(),
    reply,
    chain,
    from: target.to_be_bytes().to_vec(),
  };

  let room_id = SERVER.room_id(room_address);
  let packet = Packet::new(room_id, message.tl())?;
  SERVER.send(packet, &arcstr::literal!("mesagisto")).await?;
  Ok(())
}
