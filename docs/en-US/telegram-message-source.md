# telegram-mesaga-fonto 
** Part of [Mesagisto Project](https://github.com/MeowCat-Studio/mesagisto), the [Telegram](https://core.telegram.org) implementation of the message forwarding client. **

## Deploy instruction

 1. Get binary file(shortly called tmf) at [Release page](https://github.com/MeowCat-Studio/telegram-mesaga-fonto/releases) 
 2. Place it in where network access to Telegram server is stable (you may need HTTP proxy).
 3. Run tmf, which automatically generates the default configuration file `config.toml`
 4. Use your favorite editor to edit `config.toml`.
 here is an example:
 ```toml
# before use, set `enabled` to `true`.
enabled = true

[forwarding]
address = 'nats://itsusinn.site:4222'

[telegram]
# do not forget to fill your bot token before use
token = 'BOT_TOKEN'
# the id of your bot
bot_name = 'BOT_NAME'

[proxy]
# whether to enable proxy
enabled = false
# only http proxy is allowed at this stage (reqwest library restriction)
address = 'http://127.0.0.1:8889'

[target_address_mapper_storage]
# default empty, manually add is not recommended.
# format:
# <chat_id>= '<channel>' (int= 'int')
# example:
# -11451419 = '10000'
 ```
 4. Start the server:
 ```shell
 $ ./tmf
  [INFO] telegram-message-fonto started.	
 ```
 If there are no [ERROR], then you can send `/help` to your bot, with reply:
```text
 信使Bot支持以下命令
 /help - 显示命令帮助
 /enable - 启用消息转发
 /disable - 禁用消息转发
 /setaddress - 设置当前Group的转发地址
```
 5. Create a Telegram group, add your bot, and type in the group:

 `/setaddress <channel>`

> If you are using mirai source, the value of channel should be qq number
>
> In fact, whatever the value of the channel is, just make sure the value is the same for different forwarding clients

## Attention

Your Bot should have Group Privacy set to ON, otherwise the Bot will not be able to access the group chat messages.