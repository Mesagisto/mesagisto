use std::sync::Arc;

use arcstr::ArcStr;
use color_eyre::eyre::{Error, Result};
use dashmap::DashMap;
use figment_wrapper::{figment_derive, FigmentWrapper};

#[figment_derive]
#[derive(FigmentWrapper)]
#[location = "config/onebot.toml"]
pub struct Config {
    #[educe(Default = false)]
    pub enable: bool,
    #[educe(Default = "")]
    pub locale: ArcStr,
    // A-z order
    pub bindings: DashMap<String, ArcStr>,
    pub cipher: CipherConfig,
    pub auto_update: AutoUpdateConfig,
    pub centers: Arc<DashMap<ArcStr, ArcStr>>,
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
