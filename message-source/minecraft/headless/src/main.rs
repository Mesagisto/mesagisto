pub mod data;
pub mod exts;
pub mod game;
mod handlers;
mod log;
mod login;

use color_eyre::eyre;
use steven_protocol::protocol::packet::Packet;


use crate::{game::Client, login::bot_user};

const TARGET: &str = "mesagisto";

#[tokio::main]
async fn main() -> eyre::Result<()> {
  if cfg!(feature = "color") {
    color_eyre::install()?;
  } else {
    color_eyre::config::HookBuilder::new()
      .theme(color_eyre::config::Theme::new())
      .install()?;
  }

  // enable_network_debug();
  self::log::init();
  let client = Client::new(
    340,
    bot_user().await?,
  );
  let mut server = client.connect_to("127.0.0.1:25565").await?;
  let read_rx = server.read_queue.take().unwrap();
  let write = server.conn.take().unwrap();
  let (write_tx, write_rx) = tokio::sync::mpsc::unbounded_channel::<Packet>();
  tokio::spawn(async move {
    crate::handlers::write::handler(write, write_rx)
      .await
      .unwrap();
  });
  let clone_write_tx = write_tx.clone();
  tokio::spawn(async move {
    crate::handlers::init(client, read_rx, clone_write_tx)
      .await
      .unwrap();
  });
  tokio::signal::ctrl_c().await?;
  Ok(())
}
