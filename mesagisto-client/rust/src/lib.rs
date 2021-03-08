#![feature(fn_traits, trait_alias)]
#![feature(async_closure)]
#![feature(let_chains)]
#![feature(slice_pattern)]
use std::{
  fmt::{self, Debug, Formatter},
  ops::ControlFlow,
  sync::Arc,
};

use arcstr::ArcStr;
use cipher::CIPHER;
use color_eyre::eyre::Result;
use dashmap::DashMap;
use data::Packet;
use db::DB;
use educe::Educe;
use futures_util::future::BoxFuture;
use i18n::LANGUAGE_LOADER;
use net::NET;
use once_cell::sync::Lazy;
use res::RES;
use server::SERVER;
use uuid::Uuid;

pub mod cipher;
pub mod data;
pub mod db;
pub mod error;
pub mod net;
pub mod res;
pub mod server;

mod i18n;

#[macro_use]
extern crate singleton;
#[macro_use]
extern crate derive_builder;

const NAMESPACE_MSGIST: Uuid = Uuid::from_u128(31393687336353710967693806936293091922);

#[derive(Educe, Builder)]
#[educe(Default, Debug)]
#[builder(setter(into))]
pub struct MesagistoConfig {
  #[educe(Default = "default")]
  pub name: ArcStr,
  pub proxy: Option<ArcStr>,
  pub cipher_key: ArcStr,
  pub remote_address: Option<ArcStr>,
}
impl MesagistoConfig {
  pub async fn apply(self) -> Result<()> {
    Lazy::force(&LANGUAGE_LOADER);
    DB.init(self.name.some());
    CIPHER.init(&self.cipher_key)?;
    RES.init().await;
    SERVER.init(self.remote_address).await?;
    NET.init(self.proxy);
    Ok(())
  }

  pub fn packet_handler<F>(resolver: F)
  where
    F: Fn(Packet) -> BoxFuture<'static, Result<ControlFlow<Packet>>> + Send + Sync + 'static,
  {
    let h = Box::new(resolver);
    SERVER.packet_handler.init(h);
  }
}

pub trait ResultExt<T, E> {
  fn ignore(self) -> Option<T>;
  fn log(self) -> Option<T>;
}
impl<T, E: Debug> ResultExt<T, E> for Result<T, E> {
  #[inline]
  fn ignore(self) -> Option<T> {
    match self {
      Ok(v) => Some(v),
      Err(_) => None,
    }
  }

  #[inline(always)]
  fn log(self) -> Option<T> {
    match self {
      Ok(v) => Some(v),
      Err(e) => {
        tracing::error!("{:?}", e);
        None
      }
    }
  }
}

pub trait OkExt<E> {
  #[inline]
  fn ok(self) -> Result<Self, E>
  where
    Self: Sized,
  {
    Ok(self)
  }
}
impl<T, E> OkExt<E> for T {}

pub trait OptionExt {
  #[inline]
  fn some(self) -> Option<Self>
  where
    Self: Sized,
  {
    Some(self)
  }
  #[inline]
  fn some_ref(&self) -> Option<&Self>
  where
    Self: Sized,
  {
    Some(self)
  }
}
impl<T> OptionExt for T {}

pub fn fmt_bytes(vec: &Vec<u8>, formater: &mut Formatter) -> fmt::Result {
  formater.write_str(&hex::encode(vec))?;
  Ok(())
}
