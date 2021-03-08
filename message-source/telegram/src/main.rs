#![feature(let_chains)]
#![feature(trait_alias)]
#![feature(type_alias_impl_trait)]
#![feature(impl_trait_in_assoc_type)]
#![feature(if_let_guard)]

use std::ops::Deref;

use bot::TG_BOT;
use color_eyre::eyre::Result;
use futures_util::FutureExt;
use locale_config::Locale;
use mesagisto_client::{MesagistoConfig, MesagistoConfigBuilder};
use once_cell::sync::Lazy;
use res::TG_RES;
use self_update::Status;
use teloxide::{prelude::*, types::ParseMode, Bot};

use crate::{
  config::{Config, CONFIG},
  handlers::receive::packet_handler,
};

#[macro_use]
extern crate educe;
#[macro_use]
extern crate singleton;

mod bot;
pub mod commands;
mod config;
mod dispatch;
pub mod ext;
mod handlers;
mod i18n;
mod log;
mod net;
pub mod res;
mod update;
mod webhook;

#[tokio::main(flavor = "multi_thread")]
async fn main() -> Result<()> {
  if cfg!(feature = "color") {
    color_eyre::install()?;
  } else {
    color_eyre::config::HookBuilder::new()
      .theme(color_eyre::config::Theme::new())
      .install()?;
  }
  self::log::init().await?;
  run().await?;
  Ok(())
}

async fn run() -> Result<()> {
  Config::reload().await?;
  if !CONFIG.locale.is_empty() {
    let locale = Locale::new(&*CONFIG.locale)?;
    Locale::set_global_default(locale);
  }
  Lazy::force(&i18n::LANGUAGE_LOADER);
  if !CONFIG.enable {
    warn!("log-not-enable");
    warn!("log-not-enable-helper");
    return Ok(());
  }
  CONFIG.migrate();

  if CONFIG.auto_update.enable {
    tokio::task::spawn_blocking(|| {
      match update::update() {
        Ok(Status::UpToDate(_)) => {
          info!("log-update-check-success");
        }
        Ok(Status::Updated(_)) => {
          info!("log-upgrade-success");
          std::process::exit(0);
        }
        Err(e) => {
          tracing::error!("{}", e);
        }
      };
    })
    .await?;
  }

  MesagistoConfigBuilder::default()
    .name("tg")
    .cipher_key(CONFIG.cipher.key.clone())
    .remote_address(CONFIG.deref().centers.to_owned())
    .proxy(if CONFIG.proxy.enable {
      Some(CONFIG.proxy.address.clone())
    } else {
      None
    })
    .build()?
    .apply()
    .await?;
  TG_RES.init().await;
  MesagistoConfig::packet_handler(|pkt| async { packet_handler(pkt).await }.boxed());
  info!("log-boot-start", version = env!("CARGO_PKG_VERSION"));
  let bot = Bot::with_client(CONFIG.telegram.token.clone(), net::client_from_config())
    .parse_mode(ParseMode::Html);

  TG_BOT.init(bot).await?;

  handlers::receive::recover().await?;
  tokio::spawn(async {
    dispatch::start(&TG_BOT).await;
  });
  tokio::signal::ctrl_c().await?;
  CONFIG.save().await.expect("保存配置文件失败");
  info!("log-shutdown");

  Ok(())
}
