use chrono::{Local, Offset, TimeZone};
use color_eyre::eyre::Result;
use tracing::Level;
use tracing_error::ErrorLayer;
use tracing_subscriber::{layer::SubscriberExt, prelude::*};

pub(crate) async fn init() -> Result<()> {
  let mut filter = tracing_subscriber::filter::Targets::new()
    .with_target("mesagisto_center", Level::TRACE)
    .with_target("tokio_tungstenite", Level::INFO)
    .with_default(Level::WARN);

  if cfg!(feature = "tokio-console") {
    filter = filter
      .with_target("tokio", Level::TRACE)
      .with_target("runtime", Level::TRACE);
  }
  let registry = tracing_subscriber::registry();

  let registry = registry.with(filter).with(ErrorLayer::default()).with(
    tracing_subscriber::fmt::layer()
      .with_target(true)
      .with_timer(tracing_subscriber::fmt::time::OffsetTime::new(
        time::UtcOffset::from_whole_seconds(
          Local
            .timestamp_opt(0, 0)
            .unwrap()
            .offset()
            .fix()
            .local_minus_utc(),
        )
        .unwrap_or(time::UtcOffset::UTC),
        time::macros::format_description!(
          "[year repr:last_two]-[month]-[day] [hour]:[minute]:[second]"
        ),
      )),
  );

  #[cfg(feature = "tokio-console")]
  registry.with(console_subscriber::spawn()).init();
  #[cfg(not(feature = "tokio-console"))]
  registry.init();
  Ok(())
}
