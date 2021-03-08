use chrono::{Local, Offset, TimeZone};
use color_eyre::eyre::Result;
use tracing::Level;
use tracing_error::ErrorLayer;
use tracing_subscriber::{layer::SubscriberExt, prelude::*};

pub(crate) async fn init() -> Result<()> {
  #[cfg(debug_assertions)]
  let filter = tracing_subscriber::filter::Targets::new()
    .with_target("msgist_tg", Level::TRACE)
    .with_target("mesagisto_client", Level::TRACE)
    .with_target("msgist", Level::TRACE)
    .with_target("teloxide", Level::TRACE)
    .with_default(Level::INFO);
  #[cfg(not(debug_assertions))]
  let filter = tracing_subscriber::filter::Targets::new()
    .with_target("msgist_tg", Level::DEBUG)
    .with_target("mesagisto_client", Level::DEBUG)
    .with_target("teloxide", Level::INFO)
    .with_default(Level::WARN);

  let registry = tracing_subscriber::registry();
  registry
    .with(filter)
    .with(ErrorLayer::default())
    .with(
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
    )
    .try_init()?;
  Ok(())
}
