use arcstr::ArcStr;
use educe::Educe;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Educe)]
#[educe(Debug)]
#[serde(rename_all = "snake_case")]
#[serde(tag = "t")]
#[non_exhaustive]
pub enum Event {
  RequestImage {
    #[serde(with = "serde_bytes")]
    // #[educe(Debug(method = "fmt_bytes"))]
    id: Vec<u8>,
  },
  RespondImage {
    #[serde(with = "serde_bytes")]
    // #[educe(Debug(method = "fmt_bytes"))]
    id: Vec<u8>,
    url: ArcStr,
  },
  RequestEcho {
    // should contains group_id, group_name
    name: ArcStr,
  },
  RespondEcho {
    // should contains group_id, group_name
    name: ArcStr,
  },
}

#[cfg(test)]
mod test {
  use crate::data::events::*;
  #[test]
  fn test() {
    let event = Event::RequestImage {
      id: "dd".as_bytes().to_owned(),
    };
    let mut data = Vec::new();
    ciborium::ser::into_writer(&event, &mut data).unwrap();
    println!("{} \n check in http://cbor.me/", hex::encode(&data));
    let a = ciborium::de::from_reader::<Event, &[u8]>(&data).is_ok();
    assert!(a);
  }
}
