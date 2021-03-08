use arcstr::ArcStr;
use color_eyre::eyre::Result;
use teloxide::{
  prelude::*,
  utils::{command::BotCommands, html},
};

use crate::{bot::BotRequester, config::CONFIG, handlers};

#[derive(BotCommands, Clone)]
#[command(
  rename_rule = "lowercase",
  description = "MesagistoTG supports following commands"
)]
pub enum BindCommand {
  #[command(description = "About")]
  About,
  #[command(description = "Unbind the currunt's gruop binding")]
  Unbind,
  #[command(description = "Disaplay commands help")]
  Help,
  #[command(description = "Disaplay status")]
  Status,
  #[command(description = "Bind currunt's group to address")]
  Bind { address: String },
}
impl BindCommand {
  pub async fn answer(msg: Message, bot: BotRequester, cmd: BindCommand) -> Result<()> {
    match cmd {
      BindCommand::Help => {
        bot
          .send_message(msg.chat.id, BindCommand::descriptions().to_string())
          .await?;
      }
      BindCommand::Bind { address } => {
        let sender_id = msg.from().unwrap().id;
        let chat_id = msg.chat.id;
        let admins = bot.get_chat_administrators(chat_id).await?;
        let mut is_admin = false;
        for admin in admins {
          if admin.user.id == sender_id {
            is_admin = true;
            break;
          }
        }
        if is_admin {
          match CONFIG
            .bindings
            .insert(chat_id.0.to_string(), ArcStr::from(address.clone()))
          {
            Some(before) => {
              bot
                .send_message(
                  msg.chat.id,
                  format!("成功重新绑定当前群组的信使地址为{address}"),
                )
                .await?;
              handlers::receive::change(&before, &ArcStr::from(address)).await?;
            }
            None => {
              bot
                .send_message(msg.chat.id, format!("成功绑定当前群组的信使地址{address}"))
                .await?;
              handlers::receive::add(&ArcStr::from(address)).await?;
            }
          }
        } else {
          bot
            .send_message(chat_id, "权限不足,拒绝设置信使频道")
            .await?;
        }
      }
      BindCommand::Unbind => {
        let sender_id = msg.from().unwrap().id;
        let chat_id = msg.chat.id;
        let admins = bot.get_chat_administrators(chat_id).await?;
        let mut is_admin = false;
        for admin in admins {
          if admin.user.id == sender_id {
            is_admin = true;
            break;
          }
        }
        if is_admin {
          match CONFIG.bindings.remove(&chat_id.0.to_string()) {
            Some(before) => {
              bot
                .send_message(msg.chat.id, "成功解绑当前群组的信使地址".to_string())
                .await?;
              handlers::receive::del(&before.1).await?;
            }
            None => {
              bot
                .send_message(msg.chat.id, "当前群组没有设置信使地址".to_string())
                .await?;
            }
          }
        } else {
          bot
            .send_message(chat_id, "权限不足,拒绝解绑信使频道")
            .await?;
        }
      }
      BindCommand::About => {
        let chat_id = msg.chat.id;
        bot
          .send_message(
            chat_id,
            format!(
              "GitHub项目主页:{} \n 本消息源版本 v{}",
              html::link(
                "https://github.com/MeowCat-Studio/mesagisto",
                "MeowCat-Studio/mesagisto"
              ),
              env!("CARGO_PKG_VERSION"),
            ),
          )
          .await?;
      }
      BindCommand::Status => {
        let chat_id = msg.chat.id;
        bot
          .send_message(chat_id, html::strike("唔... 也许是在正常运行?"))
          .await?;
      }
    };
    Ok(())
  }
}
