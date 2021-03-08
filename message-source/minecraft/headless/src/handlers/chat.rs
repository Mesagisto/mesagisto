use std::sync::Arc;

use steven_protocol::protocol::packet::{self, Packet};
use tokio::sync::mpsc::UnboundedSender;
use tracing::{debug, trace};

use crate::game::Client;

use super::PacketHandler;

pub fn chat_handler() -> PacketHandler {
  dptree::filter(|pkt: Arc<Packet>| {
    let mut pass = false;
    match pkt.as_ref() {
      Packet::ServerMessage_Sender(_) => pass = true,
      Packet::ServerMessage_Position(_) => pass = true,
      Packet::ServerMessage_NoPosition(_) => pass = true,
      _ => {}
    }
    pass
  })
  .endpoint(
    |pkt: Arc<Packet>, write_tx: UnboundedSender<Packet>, client: Arc<Client>,| async move {
      match pkt.as_ref().to_owned() {
        Packet::ServerMessage_Position(v) => {
          if v.position != 0 {
            trace!("Not a message from player{:#?}", v);
          }
          let msg = match v.message {
            steven_protocol::format::Component::Text(v) => v,
          };
          let extra: Vec<String> = msg
            .modifier
            .extra
            .unwrap_or_else(|| Vec::new())
            .into_iter()
            .filter_map(|v| {
              let msg = match v {
                steven_protocol::format::Component::Text(v) => v.text,
              };
              if !msg.is_empty() { Some(msg) } else { None }
            })
            .collect();
          let mut all = if msg.text.is_empty() { Vec::new() } else { vec![msg.text] };
          all.extend(extra);
          if all.is_empty() || all.iter().any(|s| s.contains(&client.profile.username) || s.contains("unhandled: ") ) { return Ok(()) }
          for msg in all {
            write_tx.send(
              packet::play::serverbound::ChatMessage {
                message: msg,
              }
              .into(),
            )?;
          }
        }
        _ => {}
      }
      Ok(())
    },
  )
}
