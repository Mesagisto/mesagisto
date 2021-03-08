use std::sync::Arc;

use arcstr::ArcStr;
use color_eyre::eyre::{Error, Result};
use dashmap::DashMap;
use figment_wrapper::{figment_derive, FigmentWrapper};
use mesagisto_client::server::SERVER;
use uuid::Uuid;

#[figment_derive]
#[derive(FigmentWrapper)]
#[location= "config/dc.yml"]
pub struct Config {
  #[educe(Default = false)]
  pub enable: bool,
  #[educe(Default = "")]
  pub locale: ArcStr,
  pub auto_update: AutoUpdateConfig,
  pub discord: DiscordConfig,
  pub proxy: ProxyConfig,
  pub cipher: CipherConfig,
  pub bindings: DashMap<String, ArcStr>,
  pub tls: TlsConfig,
  pub centers: Arc<DashMap<ArcStr, ArcStr>>,

}
impl Config {
  pub fn room_address(&self, target: &u64) -> Option<ArcStr> {
    self.bindings.get(&target.to_string()).map(|v| v.clone())
  }

  pub fn room_id(&self, target: u64) -> Option<Arc<Uuid>> {
    let room_address = self.room_address(&target)?;
    Some(SERVER.room_id(room_address))
  }

  pub fn target_id(&self, room_id: Arc<Uuid>) -> Option<Vec<u64>> {
    let entry = SERVER.room_map.iter().find(|v| v.value() == &room_id)?;
    let room_address = entry.key();
    let targets = self
      .bindings
      .iter()
      .filter_map(|v| {
        if v.value() == room_address {
          Some(v.key().parse::<u64>().expect("Key in config is not number!").to_owned())
        } else {
          None
        }
      })
      .collect::<Vec<_>>();
    Some(targets)
  }
}


#[figment_derive]
pub struct DiscordConfig {
  #[educe(Default = "BOT.TOKEN")]
  pub token: String,
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
pub struct AutoUpdateConfig {
  #[educe(Default = true)]
  pub enable: bool,
  #[educe(Default = true)]
  pub enable_proxy: bool,
  #[educe(Default = false)]
  pub no_confirm: bool,
}

#[figment_derive]
pub struct TlsConfig {
  #[educe(Default = false)]
  pub skip_verify: bool,
  pub custom_cert: ArcStr
}