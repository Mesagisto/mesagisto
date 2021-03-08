use std::sync::Arc;

use steven_protocol::protocol::packet::{self, Packet};
use tokio::sync::mpsc::UnboundedSender;
use tracing::trace;

use super::PacketHandler;

const TARGET: &str = "mesagisto::heartbeat";

pub(crate) fn heartbeat_handler() -> PacketHandler {
  dptree::filter(|pkt: Arc<Packet>| {
    let mut pass = false;
    match pkt.as_ref() {
      Packet::KeepAliveClientbound_i64(v) => {
        trace!(target: TARGET, "KeepAlive Request {:?}", v);
        pass = true;
      }
      Packet::KeepAliveClientbound_VarInt(v) => {
        trace!(target: TARGET, "KeepAlive Request {:?}", v);
        pass = true;
      }
      Packet::KeepAliveClientbound_i32(v) => {
        trace!(target: TARGET, "KeepAlive Request {:?}", v);
        pass = true;
      }
      _ => {}
    }
    pass
  })
  .endpoint(
    |pkt: Arc<Packet>, write_tx: UnboundedSender<Packet>| async move {
      match pkt.as_ref() {
        Packet::KeepAliveClientbound_i64(v) => {
          write_tx.send(packet::play::serverbound::KeepAliveServerbound_i64 { id: v.id }.into())?;
          trace!(target: TARGET, "Heartbeat Response {:?}", v.id);
        }
        Packet::KeepAliveClientbound_VarInt(v) => {
          write_tx
            .send(packet::play::serverbound::KeepAliveServerbound_VarInt { id: v.id }.into())?;
          trace!(target: TARGET, "Heartbeat Response {:?}", v.id);
        }
        Packet::KeepAliveClientbound_i32(v) => {
          write_tx.send(packet::play::serverbound::KeepAliveServerbound_i32 { id: v.id }.into())?;
          trace!(target: TARGET, "Heartbeat Response {:?}", v.id);
        }
        _ => {}
      }
      Ok(())
    },
  )
}
