use std::{ops::Deref, sync::Arc};

use arcstr::ArcStr;
use color_eyre::eyre::Result;
use lateinit::LateInit;
use mesagisto_client::res::RES;
use serde::{Deserialize, Serialize};
use serenity::{all::Http, client::Cache};

#[derive(Debug, Serialize, Deserialize)]
pub struct DcFile(u64, u64, ArcStr);
impl DcFile {
  pub fn new(channel: &u64, attachment: &u64, fname: &ArcStr) -> Self {
    Self(*channel, *attachment, fname.clone())
  }

  pub fn to_url(&self) -> ArcStr {
    format!(
      "https://cdn.discordapp.com/attachments/{}/{}/{}",
      self.0, self.1, self.2
    )
    .into()
  }
}

#[derive(Singleton, Default)]
pub struct BotClient {
  inner: LateInit<Arc<Http>>,
  cache: LateInit<Arc<Cache>>,
}
impl BotClient {
  pub fn init(&self, bot: Arc<Http>, cache: Arc<Cache>) {
    self.inner.init(bot);
    self.cache.init(cache);
  }

  pub fn cache(&self) -> Arc<Cache> {
    self.cache.clone()
  }
  pub async fn download_file(&self, dc_file: &DcFile) -> Result<()> {
    let url = dc_file.to_url();
    RES
      .file_by_url(&dc_file.1.to_be_bytes().to_vec(), &url)
      .await?;
    Ok(())
  }
}
impl Deref for BotClient {
  type Target = Arc<Http>;

  fn deref(&self) -> &Self::Target {
    &self.inner
  }
}
