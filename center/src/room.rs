use std::{ops::Deref, sync::Arc};

use dashmap::DashMap;
use singleton::Singleton;
use tokio::sync::mpsc::error::TrySendError;
use tokio_tungstenite::tungstenite;
use uuid::Uuid;

use crate::{
  ext::ResultExt,
  server::{WsConn, WsConnId},
};

#[derive(Singleton, Default)]
pub struct Rooms {
  pub inner: DashMap<Arc<Uuid>, Room>,
}
impl Rooms {
  pub async fn send(&self, room: Arc<Uuid>, conn_id: WsConnId, pkt: Vec<u8>) {
    if let Some(room) = self.inner.get(&room) {
      room.send(conn_id, pkt).await;
    };
  }

  pub fn join(&self, room: Arc<Uuid>, conn: WsConn, conn_id: WsConnId) {
    let room = self.inner.entry(room).or_insert_with(Default::default);
    room.memebers.insert(conn_id, conn);
  }

  pub fn leave(&self, room: Arc<Uuid>, conn_id: WsConnId) {
    let room = self.inner.entry(room).or_insert_with(Default::default);
    room.memebers.remove(&conn_id);
  }
}

#[derive(Singleton, Default)]
pub struct Room {
  pub memebers: DashMap<WsConnId, WsConn>,
}

impl Room {
  pub async fn send(&self, sender_id: WsConnId, pkt: Vec<u8>) {
    let pkt = Arc::new(pkt);

    let mut for_remove = vec![];
    for member in &self.memebers {
      let member_id = member.key().clone();
      let conn = member.value();
      if member_id == sender_id {
        continue;
      }
      trace!("send to ws member {}", member_id);
      match conn.try_send(tungstenite::Message::Binary(pkt.deref().to_owned())) {
        Ok(_) => {}
        Err(TrySendError::Full(msg)) => {
          // TODO add a switch
          warn!("slow receiver of ws conn");
          conn.send(msg).await.log();
        }
        Err(TrySendError::Closed(_)) => {
          info!("removing member {}", member_id);
          for_remove.push(member_id);
        }
      };
    }
    for remove in for_remove {
      self.memebers.remove(&remove);
    }
  }
}
