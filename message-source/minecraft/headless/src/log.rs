use chrono::{Local, Offset, TimeZone};
use tracing::Level;
use tracing_error::ErrorLayer;
use tracing_subscriber::prelude::*;

pub(crate) fn init() {
  let filter = tracing_subscriber::filter::Targets::new()
    // .with_target("steven_protocol", Level::DEBUG)
    .with_target("minecraft_message_source", Level::TRACE)
    .with_target("mesagisto_client", Level::TRACE)
    .with_target("mesagisto", Level::TRACE)
    .with_target("mesagisto::heartbeat", Level::INFO)
    .with_default(Level::TRACE);

  let registry = tracing_subscriber::registry()
    .with(
      tracing_subscriber::fmt::layer()
        .with_target(true)
        .with_timer(tracing_subscriber::fmt::time::OffsetTime::new(
          time::UtcOffset::from_whole_seconds(
            Local.timestamp(0, 0).offset().fix().local_minus_utc(),
          )
          .unwrap_or(time::UtcOffset::UTC),
          time::macros::format_description!(
            "[year repr:last_two]-[month]-[day] [hour]:[minute]:[second]"
          ),
        )),
    )
    .with(ErrorLayer::default())
    .with(filter);

  registry.init();
}
