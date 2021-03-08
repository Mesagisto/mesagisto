use std::{panic, path::PathBuf, time::Duration};

use arcstr::ArcStr;
use color_eyre::eyre::Result;
use dashmap::DashMap;
use lateinit::LateInit;
use sled::IVec;
use tokio::{sync::oneshot, task::JoinHandle, time::timeout};
use tracing::trace;
use uuid::Uuid;

use crate::{
  data::{events::Event, Packet},
  db::DB,
  net::NET,
  server::SERVER,
  ResultExt,
};

#[derive(Singleton, Default)]
pub struct Res {
  pub directory: LateInit<PathBuf>,
  pub handlers: DashMap<ArcStr, Vec<oneshot::Sender<PathBuf>>>,
  handle: LateInit<JoinHandle<()>>,
}
impl Res {
  pub async fn init(&self) {
    let path = {
      let mut dir = std::env::temp_dir();
      dir.push("mesagisto");
      dir
    };
    tokio::fs::create_dir_all(path.as_path()).await.unwrap();
    self.directory.init(path);
    RES.poll().await;
  }

  pub fn get(&self, name: &ArcStr) -> Option<PathBuf> {
    let path = self.path(name);
    if path.exists() {
      Some(path)
    } else {
      None
    }
  }

  async fn poll(&self) {
    let handle: JoinHandle<_> = tokio::spawn(async {
      let mut interval = tokio::time::interval(Duration::from_millis(200));
      loop {
        let mut for_remove = vec![];
        for entry in &RES.handlers {
          let path = RES.path(entry.key());
          if path.exists() {
            for_remove.push((entry.key().to_owned(), path));
          }
        }
        for_remove.into_iter().for_each(|v| {
          if let Some((.., handler_list)) = RES.handlers.remove(&v.0) {
            for handler in handler_list {
              handler.send(v.1.to_owned()).log();
            }
          }
        });
        interval.tick().await;
      }
    });
    self.handle.init(handle);
  }

  pub fn path(&self, id: &ArcStr) -> PathBuf {
    let mut path = self.directory.clone();
    path.push(id.as_str());
    path
  }

  pub fn tmp_path(&self, id: &ArcStr) -> PathBuf {
    let mut path = self.directory.clone();
    path.push(format!("{}.tmp", id));
    path
  }

  pub async fn wait_for(&self, id: &ArcStr) -> Result<PathBuf> {
    let (sender, receiver) = oneshot::channel();
    self.handlers.entry(id.clone()).or_default().push(sender);
    let path = tokio::time::timeout(Duration::from_secs_f32(13.0), receiver).await??;
    Ok(path)
  }

  pub fn put_image_id<U, F>(&self, uid: U, file_id: F)
  where
    U: AsRef<[u8]>,
    F: Into<IVec>,
  {
    DB.put_image_id(uid, file_id);
  }

  pub async fn file(
    &self,
    id: &Vec<u8>,
    url: &Option<ArcStr>,
    room: &Uuid,
    server: &ArcStr,
  ) -> Result<PathBuf> {
    match url {
      Some(url) => self.file_by_url(id, url).await,
      None => self.file_by_uid(id, room, server).await,
    }
  }

  pub async fn file_by_uid(&self, uid: &Vec<u8>, room: &Uuid, server: &ArcStr) -> Result<PathBuf> {
    use crate::data::Payload;
    let uid_str: ArcStr = base64_url::encode(uid).into();
    trace!("Caching file by uid {}", uid_str);
    let path = RES.path(&uid_str);
    if path.exists() {
      trace!("File exists,return the path");
      return Ok(path);
    }
    let tmp_path = RES.tmp_path(&uid_str);
    if tmp_path.exists() {
      trace!("TmpFile exists,waiting for the file downloading");
      return RES.wait_for(&uid_str).await;
    }
    trace!("TmpFile dont exist,requesting image url");
    let event: Event = Event::RequestImage { id: uid.clone() };
    // fixme error handling
    let packet = Packet::new(room.to_owned(), event.into())?;
    // fixme timeout check
    let packet = timeout(Duration::from_secs(7), SERVER.request(packet, server)).await??;

    match packet.decrypt()? {
      Payload::EventPayload(Event::RespondImage { id, url }) => self.file_by_url(&id, &url).await,
      _ => panic!("Not correct response"),
    }
  }

  pub async fn file_by_url(&self, id: &Vec<u8>, url: &ArcStr) -> Result<PathBuf> {
    let id_str: ArcStr = base64_url::encode(id).into();
    let path = RES.path(&id_str);
    if path.exists() {
      return Ok(path);
    }

    let tmp_path = RES.tmp_path(&id_str);
    if tmp_path.exists() {
      Ok(RES.wait_for(&id_str).await?)
    } else {
      // fixme error handling
      NET.download(url, &tmp_path).await?;
      tokio::fs::rename(&tmp_path, &path).await?;
      Ok(path)
    }
  }

  pub async fn put_file(&self, id: &Vec<u8>, file: &PathBuf) -> Result<PathBuf> {
    let id_str: ArcStr = base64_url::encode(id).into();
    let path = RES.path(&id_str);
    tokio::fs::rename(&file, &path).await?;
    Ok(path)
  }
}
