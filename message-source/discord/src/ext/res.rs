use color_eyre::eyre::Result;
use mesagisto_client::res::Res;

use crate::bot::DcFile;

pub trait ResExt {
  fn put_dc_image_id(&self, uid: &u64, file_id: &DcFile) -> Result<()>;
}
impl ResExt for Res {
  #[inline]
  fn put_dc_image_id(&self, uid: &u64, file_id: &DcFile) -> Result<()> {
    let mut bytes = Vec::new();
    ciborium::ser::into_writer(file_id, &mut bytes)?;
    self.put_image_id(uid.to_be_bytes(), bytes);
    Ok(())
  }
}
