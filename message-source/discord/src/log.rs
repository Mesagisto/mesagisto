use chrono::{Local, Offset, TimeZone};
use tracing::Level;
use tracing_subscriber::prelude::*;

pub(crate) fn init() {
  let filter = tracing_subscriber::filter::Targets::new()
    .with_target("serenity", Level::WARN)
    .with_target("msgist_dc", Level::DEBUG)
    .with_target("mesagisto_client", Level::DEBUG)
    .with_target("tokio_tungstenite", Level::DEBUG)
    .with_target("tungstenite", Level::DEBUG)
    .with_default(Level::WARN);

  let registry = tracing_subscriber::registry()
    .with(
      tracing_subscriber::fmt::layer()
        .with_target(true)
        .with_timer(tracing_subscriber::fmt::time::OffsetTime::new(
          time::UtcOffset::from_whole_seconds(
            Local.timestamp_opt(0, 0).unwrap().offset().fix().local_minus_utc(),
          )
          .unwrap_or(time::UtcOffset::UTC),
          time::macros::format_description!(
            "[year repr:last_two]-[month]-[day] [hour]:[minute]:[second]"
          ),
        )),
    )
    .with(filter);
  registry.init();
}
