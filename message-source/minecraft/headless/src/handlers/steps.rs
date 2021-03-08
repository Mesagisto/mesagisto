use std::{
  sync::{atomic::Ordering, Arc},
  time::Duration,
};

use color_eyre::eyre;
use steven_protocol::protocol::{
  self,
  packet::{self, Packet},
};
use tokio::sync::mpsc::UnboundedSender;
use tracing::trace;

use super::PacketHandler;
use crate::game::STEP;

const TARGET: &str = "mesagisto::steps";

pub fn step_10() -> PacketHandler {
  dptree::filter(|pkt: Arc<Packet>| {
    let mut pass = false;
    match pkt.as_ref() {
      Packet::JoinGame_WorldNames_IsHard(_) => pass = true,
      Packet::JoinGame_WorldNames(_) => pass = true,
      Packet::JoinGame_HashedSeed_Respawn(_) => pass = true,
      Packet::JoinGame_i32(_) => pass = true,
      Packet::JoinGame_i32_ViewDistance(_) => pass = true,
      Packet::JoinGame_i8(_) => pass = true,
      _ => {}
    }
    pass
  })
  .endpoint(|write_tx: UnboundedSender<Packet>| async move {
    assert!(STEP.fetch_max(10, Ordering::Relaxed) < 10);
    step_15(write_tx).await?;
    Ok(())
  })
}

pub async fn step_15(write_tx: UnboundedSender<Packet>) -> eyre::Result<()> {
  tokio::time::sleep(Duration::from_secs(1)).await;
  assert!(STEP.fetch_max(15, Ordering::Relaxed) < 15);

  trace!(target: TARGET, "step15 C->S Client Information");

  write_tx.send(
    packet::play::serverbound::ClientSettings {
      locale: "en_GB".to_string(),
      view_distance: 2,
      chat_mode: protocol::VarInt(0),
      chat_colors: false,
      displayed_skin_parts: 0,
      main_hand: protocol::VarInt(0),
    }
    .into(),
  )?;
  Ok(())
}
pub async fn step_33(write_tx: UnboundedSender<Packet>) -> eyre::Result<()> {
  trace!(target: TARGET, "step33 C->S Client Command");
  write_tx.send(
    packet::play::serverbound::ClientStatus {
        action_id: protocol::VarInt(0),
    }.into()
  )?;
  Ok(())
}