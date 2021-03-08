pub mod chat;
mod heartbeat;
pub mod steps;
pub mod write;

use std::sync::Arc;

use color_eyre::eyre::Result;
use dptree::prelude::*;
use steven_protocol::protocol::{
  self,
  packet::{self, Packet},
};
use tokio::sync::mpsc::{UnboundedReceiver, UnboundedSender};
use tracing::trace;

use crate::game::Client;

use self::heartbeat::heartbeat_handler;

const TARGET: &str = "mesagisto::handlers";

type PacketHandler = Endpoint<'static, DependencyMap, Result<()>>;

pub async fn init(
  client: Client,
  mut read_rx: UnboundedReceiver<Result<Packet, protocol::Error>>,
  write_tx: UnboundedSender<Packet>,
) -> Result<()> {
  let packet_handler = dptree::entry()
    .branch(heartbeat_handler())
    .branch(steps::step_10())
    .branch(chat::chat_handler())
    .branch(default_handler());

  let client = Arc::new(client);
  while let Some(packet) = read_rx.recv().await {
    let packet = Arc::new(packet?);
    let ctrl_flow = packet_handler
      .dispatch(dptree::deps![packet, write_tx.clone(), client.clone()])
      .await;
    match ctrl_flow {
      ControlFlow::Continue(_) => {
        trace!("ControlFlow Contine");
        continue;
      }
      ControlFlow::Break(result) => {
        // trace!("ControlFlow Break");
        let _ = result?;
        continue;
      }
    }
  }
  Ok(())
}

fn default_handler() -> PacketHandler {
  dptree::endpoint(
    |pkt: Arc<Packet>, write: UnboundedSender<Packet>| async move {
      match pkt.as_ref() {
        Packet::TeleportPlayer_WithDismount(v) => {
          trace!(target: TARGET, "step 31 C->S Accept Teleportation");
          write.send(
            packet::play::serverbound::TeleportConfirm {
              teleport_id: v.teleport_id,
            }
            .into(),
          )?;
          steps::step_33(write).await?;
        }
        Packet::TeleportPlayer_WithConfirm(v) => {
          trace!(target: TARGET, "step 31 C->S Accept Teleportation");
          write.send(
            packet::play::serverbound::TeleportConfirm {
              teleport_id: v.teleport_id,
            }
            .into(),
          )?;
          steps::step_33(write).await?;
        }

        _ => {}
      }
      Ok(())
    },
  )
}
