use std::{fs::File, io::BufReader, path::PathBuf};

use image::ImageFormat;

use crate::res::ConverterFn;
pub mod db;
pub mod err;
pub mod res;

use color_eyre::{eyre, eyre::Result};
use futures_util::Future;

pub struct WebpConverter;

impl ConverterFn for WebpConverter {
  type ConverterFuture = impl Future<Output = Result<()>>;

  fn invoke(&self, before: PathBuf, after: PathBuf) -> Self::ConverterFuture {
    convert_to_webp(before, after)
  }
}

async fn convert_to_webp(before: PathBuf, after: PathBuf) -> Result<()> {
  let result: Result<()> = tokio::task::spawn_blocking(|| {
    let buffered_read = BufReader::new(File::open(&before)?);
    let format = infer::get_from_path(before)?.expect("Unable to infer image type");
    let format = match format.extension() {
      "png" => ImageFormat::Png,
      "jpg" => ImageFormat::Jpeg,
      "gif" => ImageFormat::Gif,
      "webp" => ImageFormat::WebP,
      "bmp" => ImageFormat::Bmp,
      _ => return Err(eyre::eyre!("Unsupported image type")),
    };

    let before = image::load(buffered_read, format)?;

    before.save_with_format(after, ImageFormat::WebP)?;
    Ok(())
  })
  .await
  .expect("Tokio JoinHandle Error");
  result?;
  Ok(())
}
