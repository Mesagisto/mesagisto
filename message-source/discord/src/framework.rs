use std::collections::HashSet;

use serenity::{
  client::Context,
  framework::standard::{
    help_commands,
    macros::{group, help},
    Args, CommandGroup, CommandResult, HelpOptions,
  },
  model::{channel::Message, id::UserId},
};

use crate::commands::*;

#[group("**Commands**|**命令列表**")]
#[description = "Commands of Mesagisto"]
#[commands(channel)]
pub struct Mesagisto;

#[help]
#[individual_command_tip = "Hello! こんにちは！Hola! Bonjour! 您好! 안녕하세요!\n
> If you want more information about a specific command, just pass the command as argument.
> 如果你想获得关于某个特定命令的更多信息，只需将该命令作为help命令的参数传入。"]
#[command_not_found_text = "Could not find: `{}`."]
#[strikethrough_commands_tip_in_dm = "
> ~~`Strikethrough commands`~~ are unavailable because they require permissions, require a \
                                      specific role, require certain conditions.
> ~~`带删除线的命令`~~无法使用，因为它们需要权限、特定的角色、某些条件等。"]
#[strikethrough_commands_tip_in_guild = "
> ~~`Strikethrough commands`~~ are unavailable because they require permissions, require a \
                                         specific role, require certain conditions.
> ~~`带删除线的命令`~~无法使用，因为它们需要权限、特定的角色、某些条件等。"]
#[max_levenshtein_distance(3)]
#[lacking_permissions = "Hide"]
#[lacking_role = "Hide"]
#[wrong_channel = "Strike"]
#[group_prefix = "Prefix commands"]
pub async fn help(
  context: &Context,
  msg: &Message,
  args: Args,
  help_options: &'static HelpOptions,
  groups: &[&'static CommandGroup],
  owners: HashSet<UserId>,
) -> CommandResult {
  let _ = help_commands::with_embeds(context, msg, args, help_options, groups, owners).await;
  Ok(())
}
