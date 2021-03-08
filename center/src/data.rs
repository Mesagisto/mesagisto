use std::sync::Arc;

use serde::{Deserialize, Serialize};
use uuid::Uuid;

#[derive(Serialize, Deserialize, Clone, Debug)]
pub struct Packet {
  #[serde(rename = "rid")]
  pub room_id: Arc<Uuid>,
  #[serde(skip_serializing_if = "Option::is_none", default)]
  pub ctl: Option<Ctl>,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
#[serde(tag = "t")]
pub enum Ctl {
  #[serde(rename = "sub")]
  Sub,
  #[serde(rename = "unsub")]
  Unsub,
}
