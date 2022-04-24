# Telegram Message Source
**Part of [Mesagisto Project](https://github.com/MeowCat-Studio/mesagisto), the [Telegram](https://core.telegram.org) implementation of the message forwarding client.**

## Deploy instruction

 1. Get binary file(shortly called tmf) at [Release page](https://github.com/MeowCat-Studio/telegram-mesaga-fonto/releases) 
 2. Place it in where network access to Telegram server is stable (you may need HTTP proxy).
 3. Run tmf, which automatically generates the default configuration file `config.yml`
 4. Use your favorite editor to edit `config.yml`.
 here is an example:
 ```yaml
---
# before use, set `enabled` to `true`.
enabled: true
forwarding:
  address: "nats://itsusinn.site:4222"
telegram:
  # do not forget to fill your bot token before use
  token: "114514114:IYokoiYoT4YfU_NA9NzhS5HS5oT-oJTrE"
proxy:
  # whether to enable proxy
  enabled: true
  # only http proxy is allowed at this stage (reqwest library restriction)
  address: "http://127.0.0.1:7890"
# default empty, manually add is not recommended.
# format:
# "<chat_id>"= "<channel>" ("str"= "str")
# example:
# -11451419 = '10000'
target_address_mapper: {}
 ```
 4. Start the tms:
 ```shell
 # Giving executable permissions
 $ chmod +x ./tmf
 $ ./tmf
  INFO  telegram_mesaga_fonto > Mesagisto-Bot is starting up
  INFO  telegram_mesaga_fonto > Connecting to nats server
  INFO  telegram_mesaga_fonto > Connected sucessfully,the client id is ***
 # To close tmf, use Ctrl+C
 $ ^C
  INFO  teloxide::dispatching::dispatcher > ^C received, trying to shutdown the dispatcher...
  INFO  teloxide::dispatching::dispatcher > Dispatching has been shut down.
  INFO  telegram_mesaga_fonto::config     > Configuration file was saved
  INFO  telegram_mesaga_fonto             > Mesagisto Bot is going to shut down
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
- Your Bot should have Group Privacy Mode set to OFF, otherwise the Bot will not be able to access the group chat messages.
- After changing the Group Privacy Mode or group type, please remove the Bot from the group and restart it.
