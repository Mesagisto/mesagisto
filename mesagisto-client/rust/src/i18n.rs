use i18n_embed::{
  fluent::{fluent_language_loader, FluentLanguageLoader},
  DesktopLanguageRequester, LanguageLoader,
};
use once_cell::sync::Lazy;
use rust_embed::RustEmbed;

#[derive(RustEmbed)]
#[folder = "i18n/"]
struct Localizations;

pub static LANGUAGE_LOADER: Lazy<FluentLanguageLoader> = Lazy::new(|| {
  let loader: FluentLanguageLoader = fluent_language_loader!();
  let requested_languages = DesktopLanguageRequester::requested_languages();
  loader
    .load_fallback_language(&Localizations)
    .expect("Error while loading fallback language");
  _ = i18n_embed::select(&loader, &Localizations, &requested_languages);
  loader
});

#[macro_export]
macro_rules! fl {
  ($message_id:literal) => {{
    i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id)
  }};
  ($message_id:literal, $($args:expr),*) => {{
    i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id, $($args), *)
  }};
}
#[macro_export]
macro_rules! trace {
  ($message_id:literal) => {{
    tracing::trace!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id));
  }};
  ($message_id:literal, $($args:expr),*) => {{
    tracing::trace!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id, $($args), *));
  }};
}

#[macro_export]
macro_rules! debug {
  ($message_id:literal) => {{
    tracing::debug!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id));
  }};
  ($message_id:literal, $($args:expr),*) => {{
    tracing::debug!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id, $($args), *));
  }};
}

#[macro_export]
macro_rules! info {
  ($message_id:literal) => {{
    tracing::info!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id));
  }};
  ($message_id:literal, $($args:expr),*) => {{
    tracing::info!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id, $($args), *));
  }};
}

#[macro_export]
macro_rules! warn {
  ($message_id:literal) => {{
    tracing::warn!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id));
  }};
  ($message_id:literal, $($args:expr),*) => {{
    tracing::warn!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id, $($args), *));
  }};
}

#[macro_export]
macro_rules! error {
  ($message_id:literal) => {{
    tracing::error!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id));
  }};
  ($message_id:literal, $($args:expr),*) => {{
    tracing::error!("{}",i18n_embed_fl::fl!($crate::i18n::LANGUAGE_LOADER, $message_id, $($args), *));
  }};
}
