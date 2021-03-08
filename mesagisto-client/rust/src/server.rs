use std::sync::{
  atomic::{AtomicI64, Ordering},
  Arc,
};

use arcstr::ArcStr;
use async_recursion::async_recursion;
use color_eyre::eyre::Result;
use dashmap::DashMap;
use futures_util::{future::BoxFuture, StreamExt};
use lateinit::LateInit;
use tokio::task::JoinHandle;
use tracing::instrument;
use uuid::Uuid;

use crate::{OkExt, ResultExt};

pub trait PacketHandler =
  Fn(Packet) -> BoxFuture<'static, Result<ControlFlow<Packet>>> + Send + Sync + 'static;

use crate::{cipher::CIPHER, data::Packet, ControlFlow, NAMESPACE_MSGIST};

#[derive(Singleton, Default)]
pub struct Server {
  pub conn: LateInit<nats::Client>,
  pub remote_address: LateInit<ArcStr>,
  pub packet_handler: LateInit<Box<dyn PacketHandler>>,

  pub room_map: DashMap<ArcStr, uuid::Uuid>,
  pub subs: DashMap<Uuid, (AtomicI64, JoinHandle<()>)>,
}
impl Server {
  pub async fn init(&self, remote_address: Option<ArcStr>) -> Result<()> {
    let remote_address = remote_address.unwrap_or("itsusinn.site:4222".into());

    let client = nats::connect(remote_address.as_str()).await?;

    self.conn.init(client);
    self.remote_address.init(remote_address);

    Ok(())
  }

  pub fn room_id(&self, room_address: ArcStr) -> Uuid {
    let entry = self.room_map.entry(room_address.clone());
    *entry.or_insert_with(|| {
      let unique_address = format!("{}{}", room_address, *CIPHER.origin_key);
      Uuid::new_v5(&NAMESPACE_MSGIST, unique_address.as_bytes())
    })
  }

  #[async_recursion]
  pub async fn send(&self, pkt: Packet) -> Result<()> {
    self
      .conn
      .publish(pkt.room_id.as_hyphenated().to_string(), pkt.content.into())
      .await?;

    Ok(())
  }

  #[instrument(skip(self))]
  pub async fn sub(&self, room_id: Uuid) -> Result<()> {

      let client = self.conn.clone();
      let subs = self
        .subs
        .entry(room_id.to_owned())
        .or_insert_with(move || {
          let handle = tokio::spawn(async move {
            let mut sub = client
              .subscribe(room_id.as_hyphenated().to_string())
              .await
              .expect("Failed to subscribe");
            while let Some(next) = sub.next().await {
              let pkt = Packet {
                content: next.payload.to_vec(),
                room_id,
                reply: next.reply,
              };
              (SERVER.packet_handler)(pkt).await.log();
            }
          });
          (AtomicI64::new(0), handle)
        });
      let counter = &subs.value().0;

      counter.fetch_add(1, Ordering::SeqCst);


    Ok(())
  }

  #[instrument(skip(self))]
  pub async fn unsub(&self, room_id: &Uuid, server: &ArcStr) -> Result<()> {

    if let Some(subs) = self.subs.get(&room_id) {
      subs.0.fetch_sub(1, Ordering::SeqCst);
      if subs.0.load(Ordering::SeqCst) < 1 {
        if let Some((_, former)) = self.subs.remove(&room_id) {
          former.1.abort();
        }
      }
    }
    Ok(())
  }

  #[instrument(skip(self))]
  pub async fn request(&self, pkt: Packet, server_name: &ArcStr) -> Result<Packet> {

      let msg = self.conn
        .request(pkt.room_id.as_hyphenated().to_string(), pkt.content.into())
        .await?;
      Packet {
        content: msg.payload.into(),
        room_id: pkt.room_id,
        reply: None,
      }
      .ok()

  }

  pub async fn respond(
    &self,
    pkt: Packet,
    reply: nats::Subject
  ) -> Result<()> {
    self.conn.publish(reply, pkt.content.into()).await?;
    Ok(())
  }
}
