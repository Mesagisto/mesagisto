use std::path::PathBuf;

use arcstr::ArcStr;
use color_eyre::eyre::{self, Result};
use futures_util::Future;
use lateinit::LateInit;
use mesagisto_client::res::RES;
use tokio::fs;

#[derive(Singleton, Default)]
pub struct TgRes {
  dir: LateInit<PathBuf>,
}
pub trait ConverterFn {
  type ConverterFuture: Future<Output = Result<()>>;
  #[must_use]
  fn invoke(&self, before: PathBuf, after: PathBuf) -> Self::ConverterFuture;
}

impl TgRes {
  pub async fn init(&self) {
    let path = {
      let mut dir = std::env::temp_dir();
      dir.push("mesagisto");
      dir.push("tg");
      dir
    };
    fs::create_dir_all(path.as_path()).await.unwrap();
    self.dir.init(path);
  }

  pub async fn convert(
    &self,
    name: &ArcStr,
    variant: &ArcStr,
    converter: impl ConverterFn,
  ) -> Result<PathBuf> {
    let before = match RES.get(name) {
      Some(v) => v,
      None => return Err(eyre::eyre!("Not exists")),
    };
    let after = {
      let mut dir = self.dir.clone();
      dir.push(format!("{variant}-{name}"));
      dir
    };
    if !after.exists() {
      converter.invoke(before, after.clone()).await?;
    };
    Ok(after)
  }
}
