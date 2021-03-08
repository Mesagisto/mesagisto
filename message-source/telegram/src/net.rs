use std::time::Duration;

fn default_reqwest_settings() -> reqwest::ClientBuilder {
  reqwest::Client::builder()
    .connect_timeout(Duration::from_secs(5))
    .timeout(Duration::from_secs(17))
    .tcp_nodelay(true)
}
// TODO noproxy
pub fn client_from_config() -> reqwest::Client {
  use crate::config::CONFIG;
  let builder = default_reqwest_settings().use_rustls_tls();
  if CONFIG.proxy.enable {
    builder.proxy(
      reqwest::Proxy::all(CONFIG.proxy.address.as_str()).expect("reqwest::Proxy creation failed"),
    )
  } else {
    builder
  }
  .build()
  .expect("creating reqwest::Client")
}
