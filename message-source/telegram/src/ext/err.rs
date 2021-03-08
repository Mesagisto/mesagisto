use color_eyre::eyre::Result;

pub trait LogResultExt<T> {
  fn log_if_error(self, message: &str) -> Option<T>;
}

impl<T> LogResultExt<T> for Result<T> {
  #[inline(always)]
  fn log_if_error(self, message: &str) -> Option<T> {
    match self {
      Ok(v) => Some(v),
      Err(e) => {
        tracing::error!("{}\n{:?}", message, e,);
        None
      }
    }
  }
}
