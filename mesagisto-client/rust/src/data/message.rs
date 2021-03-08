use std::convert::TryInto;

use arcstr::ArcStr;
use serde::{Deserialize, Serialize};

use crate::{OptionExt, ResultExt};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct Profile {
  #[serde(with = "serde_bytes")]
  pub id: Vec<u8>,
  #[serde(skip_serializing_if = "Option::is_none", default)]
  pub username: Option<String>,
  #[serde(skip_serializing_if = "Option::is_none", default)]
  pub nick: Option<String>,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "snake_case")]
pub struct Message {
  pub profile: Profile,
  #[serde(with = "serde_bytes")]
  pub from: Vec<u8>,
  #[serde(with = "serde_bytes")]
  pub id: Vec<u8>,
  #[serde(with = "serde_bytes", skip_serializing_if = "Option::is_none", default)]
  pub reply: Option<Vec<u8>>,
  pub chain: Vec<MessageType>,
}
impl Message {
  pub fn new(profile: Profile, id: i32, from: Vec<u8>, chain: Vec<MessageType>) -> Self {
    Message {
      profile,
      id: id.to_be_bytes().to_vec(),
      from,
      reply: None,
      chain,
    }
  }

  pub fn id_i64(&self) -> Option<i64> {
    i64::from_be_bytes(self.id.clone().try_into().ignore()?).some()
  }
}
#[derive(Serialize, Deserialize, Debug, Clone)]
#[serde(rename_all = "snake_case", tag = "t")]
#[non_exhaustive]
pub enum MessageType {
  Text {
    content: String,
  },
  Edit {
    content: String,
  },
  Image {
    #[serde(with = "serde_bytes")]
    id: Vec<u8>,
    #[serde(skip_serializing_if = "Option::is_none", default)]
    url: Option<ArcStr>,
  },
  Sticker {
    #[serde(with = "serde_bytes")]
    id: Vec<u8>,
    #[serde(skip_serializing_if = "Option::is_none", default)]
    url: Option<ArcStr>,
  },
}

#[cfg(test)]
mod test {
  use crate::data::message::{Message, MessageType, Profile};
  #[test]
  fn test() {
    let message = Message {
      profile: Profile {
        id: 232323i32.to_be_bytes().to_vec(),
        username: None,
        nick: None,
      },
      id: Vec::from("id"),
      chain: vec![
        MessageType::Text {
          content: "this is text".to_string(),
        },
        MessageType::Text {
          content: "this is text".to_string(),
        },
        MessageType::Image {
          id: Vec::from("id"),
          url: None,
        },
      ],
      reply: None,
      from: 12113i64.to_be_bytes().to_vec(),
    };

    let mut data = Vec::new();
    ciborium::ser::into_writer(&message, &mut data).unwrap();
    println!("{} \n check in http://cbor.me/", hex::encode(&data));
    let a = ciborium::de::from_reader::<Message, &[u8]>(&data).is_ok();
    assert!(a);
  }
}
