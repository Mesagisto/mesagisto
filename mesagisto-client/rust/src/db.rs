use arcstr::ArcStr;
use color_eyre::eyre::Result;
use dashmap::DashMap;
use lateinit::LateInit;
use sled::IVec;
use tracing::error;

#[derive(Singleton, Default)]
pub struct Db {
  image_db: LateInit<sled::Db>,
  // message id
  mid_db_map: DashMap<Vec<u8>, sled::Db>,

  db_name: LateInit<ArcStr>,
}
impl Db {
  pub fn init(&self, db_name: Option<ArcStr>) {
    let db_name = db_name.unwrap_or_else(|| ArcStr::from("default"));

    let options = sled::Config::default().cache_capacity(1024 * 1024);
    let image_db_path = format!("db/{}/image", db_name);
    let image_db = options.path(image_db_path.as_str()).open().unwrap();
    self.image_db.init(image_db);

    self.db_name.init(db_name);
  }

  pub fn put_image_id<U, F>(&self, uid: U, file_id: F)
  where
    U: AsRef<[u8]>,
    F: Into<IVec>,
  {
    self.image_db.insert(uid, file_id).unwrap();
  }

  pub fn get_image_id<T>(&self, uid: T) -> Option<IVec>
  where
    T: AsRef<[u8]>,
  {
    match self.image_db.get(uid) {
      Ok(file_id) => file_id,
      Err(e) => {
        error!("{:?}", e);
        None
      }
    }
  }

  pub fn put_msg_id(
    &self,
    target: Vec<u8>,
    uid: Vec<u8>,
    id: Vec<u8>,
    reverse: bool,
  ) -> Result<()> {
    let msg_id_db = self.mid_db_map.entry(target.clone()).or_insert_with(|| {
      let options = sled::Config::default().cache_capacity(1024 * 1024);
      let msg_id_db_path = format!(
        "db/{}/msg-id/{}",
        *self.db_name,
        base64_url::encode(&target)
      );
      options.path(msg_id_db_path).open().unwrap()
    });
    msg_id_db.insert(&uid, id.clone())?;
    if reverse {
      msg_id_db.insert(&id, uid)?;
    }
    Ok(())
  }

  pub fn get_msg_id(&self, target: &[u8], id: &[u8]) -> Result<Option<Vec<u8>>> {
    let msg_id_db = match self.mid_db_map.get(target) {
      Some(v) => v,
      None => return Ok(None),
    };
    let id = match msg_id_db.get(id)? {
      Some(v) => v.to_vec(),
      None => return Ok(None),
    };
    Ok(Some(id))
  }
}
