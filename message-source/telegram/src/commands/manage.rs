use color_eyre::eyre::Result;
use teloxide::{prelude::*, utils::command::BotCommands};

use crate::bot::BotRequester;

#[derive(BotCommands, Clone)]
#[command(
  rename_rule = "lowercase",
  description = "MesagistoTG management commands"
)]
pub enum ManageCommand {
  #[command(description = "Disaplay manage commands help")]
  ManageHelp,
  #[command(description = "Add a new WS Server", parse_with = "split")]
  NewProfile { name: String, address: String },
}
impl ManageCommand {
  pub async fn answer(msg: Message, bot: BotRequester, cmd: ManageCommand) -> Result<()> {
    match cmd {
      ManageCommand::ManageHelp => {
        bot
          .send_message(msg.chat.id, ManageCommand::descriptions().to_string())
          .await?;
      }
      ManageCommand::NewProfile { name: _, address: _ } => {}
    }
    Ok(())
  }
}
