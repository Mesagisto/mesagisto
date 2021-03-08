pub mod events;
pub mod message;

use aes_gcm_siv::aead::Aead;
use color_eyre::eyre::Result;
use nats::Subject;
use serde::{Deserialize, Serialize};
use uuid::Uuid;

use self::{events::Event, message::Message};
use crate::{cipher::CIPHER, OkExt};

#[derive(Debug)]
pub struct Packet {
  pub content: Vec<u8>,
  pub room_id: Uuid,
  pub reply: Option<Subject>,
}

#[derive(Serialize, Deserialize, Debug)]
#[serde(tag = "t")]
pub enum Payload {
  #[serde(rename = "m")]
  MsgPayload(Message),
  #[serde(rename = "e")]
  EventPayload(Event),
}
impl From<Message> for Payload {
  fn from(value: Message) -> Self {
    Self::MsgPayload(value)
  }
}
impl From<Event> for Payload {
  fn from(value: Event) -> Self {
    Self::EventPayload(value)
  }
}

impl Packet {
  pub fn new(room: Uuid, payload: Payload) -> Result<Self> {
    let mut bytes = Vec::new();
    ciborium::ser::into_writer(&payload, &mut bytes)?;

    let ciphertext = CIPHER.encrypt(&CIPHER.nonce, bytes.as_ref())?;
    Self {
      content: ciphertext,
      room_id: room,
      reply: None,
    }
    .ok()
  }

  pub fn decrypt(&self) -> Result<Payload> {
    let plaintext = CIPHER.decrypt(&CIPHER.nonce, self.content.as_ref())?;
    ciborium::de::from_reader::<Payload, &[u8]>(&plaintext)?.ok()
  }
}
impl Payload {
  pub fn to_cbor(&self) -> Result<Vec<u8>> {
    let mut data = Vec::new();
    ciborium::ser::into_writer(&self, &mut data)?;
    Ok(data)
  }

  pub fn from_cbor(data: &[u8]) -> Result<Payload> {
    ciborium::de::from_reader::<Payload, &[u8]>(data)?.ok()
  }
}
#[cfg(test)]
mod test {
  use crate::{
    cipher::CIPHER,
    data::message::{self, Message},
  };
  #[test]
  fn test() {
    use crate::data::Payload;
    CIPHER.init(&"this is key".to_string().into()).unwrap();
    let message = Message {
      profile: message::Profile {
        id: 1223232i64.to_be_bytes().to_vec(),
        username: None,
        nick: None,
      },
      id: Vec::from("id"),
      reply: None,
      chain: vec![
        message::MessageType::Text {
          content: "this is text".to_string(),
        },
        message::MessageType::Text {
          content: "this is text".to_string(),
        },
      ],
      from: 12113i64.to_be_bytes().to_vec(),
    };
    let payload: Payload = message.into();
    println!("{}", hex::encode(&payload.to_cbor().unwrap()));
    let packet2 = Payload::from_cbor(&payload.to_cbor().unwrap());
    assert!(packet2.is_ok());
  }
}
