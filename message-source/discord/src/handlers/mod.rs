pub mod receive;
pub mod send;

use mesagisto_client::ResultExt;
use serenity::{
  async_trait,
  client::{Context, EventHandler},
  model::{channel::Message, prelude::Ready},
};
use tracing::info;

use crate::{handlers::send::answer_common, BOT_CLIENT};

pub struct Handler;
#[async_trait]
impl EventHandler for Handler {
  async fn ready(&self, _: Context, ready: Ready) {
    info!("Bot:{} 已连接到Discord服务器!", ready.user.name);
  }

  async fn message(&self, _: Context, msg: Message) {
    if msg.is_own(BOT_CLIENT.cache()) {
      return;
    };
    answer_common(msg).await.log();
  }
}
