use teloxide::prelude::*;

use crate::{
  bot::BotRequester,
  commands::{bind::BindCommand, manage::ManageCommand},
  fl, handlers, info,
};

pub async fn start(bot: &BotRequester) {
  let message_handler = Update::filter_message()
    .branch(
      dptree::entry()
        .filter_command::<BindCommand>()
        .endpoint(BindCommand::answer),
    )
    .branch(
      dptree::filter(|msg: Message| {
        msg.chat.is_group() || msg.chat.is_supergroup() || msg.chat.is_channel()
      })
      .endpoint(handlers::send::answer_common),
    );
  let message_handler = message_handler.branch(
    dptree::entry()
      .filter_command::<ManageCommand>()
      .endpoint(ManageCommand::answer),
  );
  let edit_message_handler = Update::filter_edited_message().branch(
    dptree::filter(|msg: Message| {
      msg.chat.is_group() || msg.chat.is_supergroup() || msg.chat.is_channel()
    })
    .endpoint(handlers::send::answer_common),
  );

  let handler = dptree::entry()
    .branch(message_handler)
    .branch(edit_message_handler);

  info!("log-boot-sucess");
  Dispatcher::builder(bot.clone(), handler)
    .error_handler(LoggingErrorHandler::with_custom_text(fl!(
      "log-callback-error"
    )))
    .build()
    .dispatch()
    .await;
}
