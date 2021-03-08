use arcstr::ArcStr;
use mesagisto_client::ResultExt;
use serenity::{
  client::Context,
  framework::standard::{macros::command, Args, CommandResult},
  model::channel::Message,
};

use crate::{config::CONFIG, handlers};

#[command]
#[required_permissions("ADMINISTRATOR")]
#[description = "Mesagisto commands about channel | 有关信使频道的指令"]
#[sub_commands(bind, unbind)]
pub async fn channel(ctx: &Context, msg: &Message) -> CommandResult {
  msg
    .channel_id
    .say(ctx, "请使用子命令 (bind, unbind, show)")
    .await?;
  Ok(())
}

#[command]
#[required_permissions("ADMINISTRATOR")]
#[description = "Set mesagisto-channel for current channel | 为当前频道设置信使频道"]
#[usage = "<channel>"]
#[min_args(1)]
pub async fn bind(ctx: &Context, msg: &Message, mut args: Args) -> CommandResult {
  if args.is_empty() {
    msg.reply(ctx, "<channel> 参数为空").await?;
  }
  let address: ArcStr = args.single::<String>().unwrap().into();
  match CONFIG
    .bindings
    .insert(msg.channel_id.get().to_string(), address.clone())
  {
    Some(before) => {
      msg
        .reply(ctx, format!("成功重新绑定当前频道的信使地址到{}", address))
        .await?;
      handlers::receive::change(&before, &address).await?;
      CONFIG.save().await.log();
    }
    None => {
      msg
        .reply(ctx, format!("成功绑定当前频道的信使地址到{}", address))
        .await?;
      handlers::receive::add(&address).await?;
      CONFIG.save().await.log();
    }
  };
  Ok(())
}

#[command]
#[required_permissions("ADMINISTRATOR")]
#[description = "Unbind mesagisto-channel for current channel | 删除当前频道的信使频道"]
pub async fn unbind(ctx: &Context, msg: &Message) -> CommandResult {
  let chat_id = msg.channel_id.get();
  match CONFIG.bindings.remove(&chat_id.to_string()) {
    Some(before) => {
      msg.reply(ctx, "成功解绑当前频道的信使地址").await?;
      handlers::receive::del(&before.1).await?;
      CONFIG.save().await.log();
    }
    None => {
      msg.reply(ctx, "当前频道没有设置信使地址").await?;
    }
  }
  Ok(())
}
