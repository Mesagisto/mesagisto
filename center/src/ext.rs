use std::fmt::Debug;

use color_eyre::eyre::ErrReport;

pub trait EyreExt<E> {
  fn to_eyre(self) -> ErrReport;
}

impl<E> EyreExt<E> for E
where
  E: Into<ErrReport> + Debug + Sync + Send,
{
  #[inline(always)]
  fn to_eyre(self) -> ErrReport {
    color_eyre::eyre::eyre!(self)
  }
}

pub trait ResultExt<T, E> {
  fn ignore(self) -> Option<T>;
  fn log(self) -> Option<T>;
  fn eyre_log(self) -> Option<T>;
}
impl<T, E> ResultExt<T, E> for Result<T, E>
where
  E: Into<ErrReport> + Debug + Sync + Send,
{
  #[inline]
  fn ignore(self) -> Option<T> {
    match self {
      Ok(v) => Some(v),
      Err(_) => None,
    }
  }

  #[inline(always)]
  fn log(self) -> Option<T> {
    match self {
      Ok(v) => Some(v),
      Err(e) => {
        error!("{:?}", e.to_eyre());
        None
      }
    }
  }

  #[inline(always)]
  fn eyre_log(self) -> Option<T> {
    match self {
      Ok(v) => Some(v),
      Err(e) => {
        error!("{:?}", e);
        None
      }
    }
  }
}

pub trait EitherExt<A> {
  #[inline]
  fn to_left(self) -> either::Either<Self, A>
  where
    Self: Sized,
  {
    either::Either::Left(self)
  }
  #[inline]
  fn tl(self) -> either::Either<Self, A>
  where
    Self: Sized,
  {
    either::Either::Left(self)
  }
  #[inline]
  fn to_right(self) -> either::Either<A, Self>
  where
    Self: Sized,
  {
    either::Either::Right(self)
  }
  #[inline]
  fn tr(self) -> either::Either<A, Self>
  where
    Self: Sized,
  {
    either::Either::Right(self)
  }
}
impl<T, A> EitherExt<A> for T {}
