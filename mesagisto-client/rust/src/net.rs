use std::{path::PathBuf, time::Duration};

use arcstr::ArcStr;
use color_eyre::eyre::Result;
use futures_util::FutureExt;
use lateinit::LateInit;
use tokio::io::AsyncWriteExt;

pub fn new_reqwest_builder() -> reqwest::ClientBuilder {
  let connect_timeout = Duration::from_secs(5);
  let timeout = connect_timeout + Duration::from_secs(12);

  reqwest::Client::builder()
    .connect_timeout(connect_timeout)
    .timeout(timeout)
    .tcp_nodelay(true)
    .use_rustls_tls()
}

#[derive(Singleton, Default)]
pub struct Net {
  inner: LateInit<reqwest::Client>,
}
impl Net {
  pub fn init(&self, proxy: Option<ArcStr>) {
    let builder = new_reqwest_builder();
    let builder = if let Some(proxy) = proxy {
      builder.proxy(reqwest::Proxy::all(proxy.as_str()).expect("reqwest::Proxy create failed"))
    } else {
      builder
    };
    self.inner.init(
      builder
        .gzip(true)
        .build()
        .expect("reqwest::Client create failed"),
    );
  }

  pub async fn download(&self, url: &ArcStr, dst: &PathBuf) -> Result<()> {
    let mut dst_file = tokio::fs::File::create(&dst).await?;
    self
      .inner
      .get(url.as_str())
      .send()
      .then(move |r| async move {
        let mut res = r?.error_for_status()?;
        while let Some(chunk) = res.chunk().await? {
          dst_file.write_all(&chunk).await?;
        }
        Ok(())
      })
      .await
  }
}
