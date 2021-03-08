use std::ops::Deref;

use color_eyre::eyre::Result;
use futures_util::FutureExt;
use locale_config::Locale;
use mesagisto_client::{MesagistoConfig, MesagistoConfigBuilder};
use once_cell::sync::Lazy;
use self_update::Status;
use serenity::{
  client::ClientBuilder, framework::standard::StandardFramework, prelude::GatewayIntents,
};

use crate::{
  bot::BOT_CLIENT,
  config::{CONFIG,Config},
  handlers::{receive, receive::packet_handler},
};

#[macro_use]
extern crate educe;

#[macro_use]
extern crate singleton;

mod bot;
mod commands;
mod config;
pub mod ext;
mod framework;
mod handlers;
mod i18n;
mod log;
mod net;
mod update;

#[tokio::main(flavor = "multi_thread")]
async fn main() -> Result<()> {
  if cfg!(feature = "color") {
    color_eyre::install()?;
  } else {
    color_eyre::config::HookBuilder::new()
      .theme(color_eyre::config::Theme::new())
      .install()?;
  }

  self::log::init();
  run().await?;
  Ok(())
}

async fn run() -> Result<()> {
  Config::reload().await?;
  if !CONFIG.locale.is_empty() {
    let locale = Locale::new(&CONFIG.locale)?;
    Locale::set_global_default(locale);
  }
  Lazy::force(&i18n::LANGUAGE_LOADER);

  if !CONFIG.enable {
    warn!("log-not-enable");
    warn!("log-not-enable-helper");
    return Ok(());
  }
  CONFIG.save().await?;

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
    .name("dc")
    .cipher_key(CONFIG.cipher.key.clone())
    .remote_address(CONFIG.deref().centers.to_owned())
    .same_side_deliver(true)
    .skip_verify(CONFIG.tls.skip_verify)
    .custom_cert(if CONFIG.tls.custom_cert.is_empty(){
      None
    } else {
      Some(CONFIG.deref().tls.custom_cert.to_owned())
    })
    .proxy(if CONFIG.proxy.enable {
      Some(CONFIG.proxy.address.clone())
    } else {
      None
    })
    .build()?
    .apply()
    .await?;
  MesagistoConfig::packet_handler(|pkt| async { packet_handler(pkt).await }.boxed());

  let framework = StandardFramework::new()
    .help(&framework::HELP)
    .group(&framework::MESAGISTO_GROUP);
  let conf = serenity::framework::standard::Configuration::default().prefix("/");
  framework.configure(conf);

  // .normal_message(handlers::message_hook);

  let http = net::build_http().await;
  let intents = {
    let mut intents = GatewayIntents::all();
    // intents.remove(GatewayIntents::GUILD_PRESENCES);
    intents.remove(GatewayIntents::DIRECT_MESSAGE_TYPING);
    intents.remove(GatewayIntents::DIRECT_MESSAGE_REACTIONS);
    intents.remove(GatewayIntents::GUILD_MESSAGE_TYPING);
    intents.remove(GatewayIntents::GUILD_MESSAGE_REACTIONS);
    intents
  };
  let mut client = ClientBuilder::new_with_http(http, intents)
    .event_handler(handlers::Handler)
    .framework(framework)
    .await?;

  BOT_CLIENT.init(client.http.clone(),client.cache.clone());

  receive::recover().await?;
  tokio::spawn(async move {
    client.start().await.expect("Client error");
  });

  tokio::signal::ctrl_c().await?;

  info!("log-shutdown");
  CONFIG.save().await?;
  Ok(())
}
