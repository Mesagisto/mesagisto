use std::sync::Arc;

use arcstr::ArcStr;
use color_eyre::eyre::{Error, Result};
use dashmap::DashMap;
use figment_wrapper::{figment_derive, FigmentWrapper};
use mesagisto_client::server::SERVER;
use uuid::Uuid;

#[figment_derive]
#[derive(FigmentWrapper)]
#[location = "config/tg.toml"]
pub struct Config {
  #[educe(Default = false)]
  pub enable: bool,
  #[educe(Default = "")]
  pub locale: ArcStr,
  // A-z order
  pub bindings: DashMap<String, ArcStr>,
  pub cipher: CipherConfig,
  pub proxy: ProxyConfig,
  pub telegram: TelegramConfig,
  pub auto_update: AutoUpdateConfig,
  pub centers: Arc<DashMap<ArcStr, ArcStr>>,
}

impl Config {
  pub fn room_address(&self, target: &i64) -> Option<ArcStr> {
    self.bindings.get(&target.to_string()).map(|v| v.clone())
  }

  // pub fn room_id(&self, target: i64) -> Option<Uuid> {
  //   let room_address = self.room_address(&target)?;
  //   Some(SERVER.room_id(room_address))
  // }

  pub fn target_id(&self, room_id: Uuid) -> Option<Vec<i64>> {
    let entry = SERVER.room_map.iter().find(|v| v.value() == &room_id)?;
    let room_address = entry.key();
    let targets = self
      .bindings
      .iter()
      .filter_map(|v| {
        if v.value() == room_address {
          Some(v.key().parse::<i64>().expect("Failed to decode channel id"))
        } else {
          None
        }
      })
      .collect::<Vec<_>>();
    Some(targets)
  }

  pub fn migrate(&self) {
    self
      .centers
      .insert("mesagisto".into(), "wss://builtin".into());
  }

  pub fn migrate_chat(&self, old_chat_id: &i64, new_chat_id: &i64) -> bool {
    if let Some((_, address)) = self.bindings.remove(&old_chat_id.to_string()) {
      self.bindings.insert(new_chat_id.to_string(), address);
      return true;
    };
    false
  }
}

#[figment_derive]
pub struct ProxyConfig {
  #[educe(Default = false)]
  pub enable: bool,
  // pattern: "http://{username}:{password}@{host}:{port}"
  #[educe(Default = "http://127.0.0.1:7890")]
  pub address: ArcStr,
}

#[figment_derive]
pub struct CipherConfig {
  #[educe(Default = "default")]
  pub key: ArcStr,
}

#[figment_derive]
pub struct TelegramConfig {
  #[educe(Default = "BOT_TOKEN")]
  pub token: String,
}

#[figment_derive]
pub struct FormatConfig {
  pub msg: ArcStr,
}

#[figment_derive]
pub struct AutoUpdateConfig {
  #[educe(Default = true)]
  pub enable: bool,
  #[educe(Default = true)]
  pub enable_proxy: bool,
  #[educe(Default = false)]
  pub no_confirm: bool,
}
