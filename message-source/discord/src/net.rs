use serenity::http::HttpBuilder;

use crate::config::CONFIG;

pub async fn build_http() -> serenity::http::Http {
  if CONFIG.proxy.enable {
    let builder = reqwest::Client::builder()
      .use_rustls_tls()
      .proxy(
        reqwest::Proxy::all(CONFIG.proxy.address.as_str())
          .expect("Failed to create reqwest::Proxy"),
      )
      .build()
      .expect("Failed to create reqwest::Client builder");
    HttpBuilder::new(CONFIG.discord.token.clone())
      .proxy(CONFIG.proxy.address.as_str())
      .ratelimiter_disabled(true)
      .client(builder)
      .build()
  } else {
    let builder = reqwest::Client::builder()
      .use_rustls_tls()
      .no_proxy()
      .build()
      .expect("Failed to create reqwest::Client builder");
    HttpBuilder::new(CONFIG.discord.token.clone())
      .ratelimiter_disabled(true)
      .client(builder)
      .build()
  }
}
