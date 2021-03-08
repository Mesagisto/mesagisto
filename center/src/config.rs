use arcstr::ArcStr;
use color_eyre::eyre::{Error, Result};

#[config_derive]
#[derive(AutomaticConfig)]
#[location = "config/center.yml"]
pub struct Config {
  #[educe(Default = false)]
  pub enable: bool,
  pub server: ServerConfig,
  pub tls: TlsConfig,
}

#[config_derive]
pub struct TlsConfig {
  #[educe(Default = false)]
  pub enable: bool,
  #[educe(Default = "/path/to/fullchain.cer")]
  pub cert: ArcStr,
  #[educe(Default = "/path/to/key")]
  pub key: String,
}

#[config_derive]
pub struct ServerConfig {
  #[educe(Default = "0.0.0.0:80")]
  pub address: ArcStr,
}
