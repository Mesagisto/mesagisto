pub mod websocket;

use std::{net::SocketAddr, sync::Arc, time::Duration};

use color_eyre::eyre::Result;
use tokio::sync::mpsc::Sender;
use tokio_tungstenite::tungstenite;

use crate::{
  data::{Ctl, Packet},
  ext::ResultExt,
  room::ROOMS,
};

pub type WsConn = Sender<tungstenite::Message>;
pub type WsConnId = u16;

pub use websocket::{ws, wss};

pub async fn receive_packets(
  data: Vec<u8>,
  conn: Sender<tungstenite::Message>,
  conn_id: u16,
) -> Result<()> {
  let pkt: Packet = ciborium::de::from_reader(&*data)?;
  #[cfg(debug_assertions)]
  info!("uni recv: {:?}", pkt.room_id.clone());
  if let Some(ctl) = &pkt.ctl {
    match ctl {
      Ctl::Sub => {
        ROOMS.join(pkt.room_id.clone(), conn, conn_id);
      }
      Ctl::Unsub => {
        ROOMS.leave(pkt.room_id.clone(), conn_id);
      }
    }
  } else {
    tokio::time::timeout(
      Duration::from_secs(7),
      ROOMS.send(pkt.room_id, conn_id, data),
    )
    .await
    .eyre_log();
  }
  Ok(())
}
