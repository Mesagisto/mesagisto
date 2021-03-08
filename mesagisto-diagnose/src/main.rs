use std::ops::ControlFlow;
use arcstr::ArcStr;
use dashmap::DashMap;
use futures_util::FutureExt;
use mesagisto_client::data::message::{MessageType, Profile};
use mesagisto_client::data::{message, Packet};
use mesagisto_client::server::SERVER;
use mesagisto_client::{EitherExt, MesagistoConfig, MesagistoConfigBuilder};
use tokio::io::{BufReader, AsyncBufReadExt};
use tracing::{info, Level};
use tracing_subscriber::{prelude::__tracing_subscriber_SubscriberExt, util::SubscriberInitExt};
use color_eyre::eyre::Result;

#[macro_use]
extern crate tracing;

#[tokio::main]
async fn main() -> Result<()> {
  if cfg!(feature = "color") {
    color_eyre::install()?;
  } else {
    color_eyre::config::HookBuilder::new()
      .theme(color_eyre::config::Theme::new())
      .install()?;
  }

  std::env::set_var("RUST_BACKTRACE", "full");
  tracing_subscriber::registry()
    .with(
      tracing_subscriber::fmt::layer()
        .with_target(true)
        .with_timer(tracing_subscriber::fmt::time::OffsetTime::new(
          // use local time
          unsafe {
            time::UtcOffset::__from_hms_unchecked(8, 0, 0)
          },
          time::macros::format_description!(
            "[year repr:last_two]-[month]-[day] [hour]:[minute]:[second]"
          ),
        )),
    )
    .with(
      tracing_subscriber::filter::Targets::new()
        .with_target("mesagisto_diagnose", Level::TRACE)
        .with_target("mesagisto_client", Level::TRACE)
        .with_default(Level::WARN),
    )
    .init();
  run().await.unwrap();
  Ok(())
}

async fn packet_handler(pkt: Packet) -> Result<ControlFlow<Packet>>{
  debug!("recv msg pkt from {:#?}", pkt.room_id);
  match pkt.decrypt()? {
    either::Either::Left(message) => {
      debug!("recv msg pkt {:#?}", message);
    },
    either::Either::Right(event) => {
      debug!("recv event pkt {:#?}", event);
    },
  }
  Ok(ControlFlow::Continue(()))
}

async fn run() -> Result<()> {
  info!("信使诊断工具启动中...");
  info!("注: 有默认项时可按下Enter使用默认项.");
  let mut line = String::new();

  info!("请输入加密密钥,默认 test");
  next_line(&mut line).await?;
  let cipher_key = if line.to_lowercase() == "" {
    "test".to_string()
  } else {
    line.trim().to_string()
  };
  info!("请输入服务器地址, 默认 wss://mesagisto.itsusinn.site/");

  next_line(&mut line).await?;
  let server_addr = if line.to_lowercase() == "" {
    "wss://mesagisto.itsusinn.site/".to_string()
  } else {
    line.trim().to_string()
  };
  let remotes = DashMap::new();
  remotes.insert(arcstr::literal!("mesagisto"), server_addr.into());
  MesagistoConfigBuilder::default()
    .name("diagnose")
    .cipher_key(cipher_key)
    .proxy(None)
    .remote_address(remotes)
    .skip_verify(true)
    .custom_cert(None)
    .same_side_deliver(false)
    .build()?
    .apply()
    .await?;
  MesagistoConfig::packet_handler(|pkt| async { packet_handler(pkt).await }.boxed());
  info!("信使诊断工具启动完成");
  info!("请输入Room地址, 默认 test");
  next_line(&mut line).await?;
  let room_address:ArcStr = if line.to_lowercase() == "" {
    "test".into()
  } else {
    line.trim().into()
  };
  let room_id = SERVER.room_id(room_address);
  let profile = Profile {
    id: 0i64.to_be_bytes().into(),
    username: Some("mesagisto-diagnose".into()),
    nick: None,
  };
  let chain = vec![MessageType::Text {
    content: "诊断工具已连接到该频道".to_string(),
  }];
  let message = message::Message {
    profile,
    id: 0i64.to_be_bytes().to_vec(),
    chain,
    reply: None,
    from: 0i32.to_be_bytes().to_vec()
  };
  let packet = Packet::new(room_id.clone(),message.tl())?;
  SERVER.send(packet,&arcstr::literal!("mesagisto")).await?;
  let packet = Packet::new_sub(room_id.clone());
  SERVER.send(packet,&arcstr::literal!("mesagisto")).await?;

  loop {
    next_line(&mut line).await?;
    let profile = Profile {
      id: 0i64.to_be_bytes().into(),
      username: Some("mesagisto-diagnose".into()),
      nick: None,
    };
    let chain = vec![MessageType::Text {
      content: line.to_string(),
    }];
    let message = message::Message {
      profile,
      id: 0i64.to_be_bytes().to_vec(),
      chain,
      reply: None,
      from: 0i32.to_be_bytes().to_vec()
    };
    let packet = Packet::new(room_id.clone(), message.tl())?;
    info!("发送消息: {}", line);
    SERVER.send(packet,&arcstr::literal!("mesagisto")).await?;
  }
}

async fn next_line(buf: &mut String) -> tokio::io::Result<usize> {
  buf.clear();
  let mut stdin = BufReader::new(tokio::io::stdin());
  let res = stdin.read_line(buf).await?;
  buf.remove(buf.len() - 1);
  Ok(res)
}
