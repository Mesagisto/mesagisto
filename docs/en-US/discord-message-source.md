# discord-mesaga-fonto 

**Part of [Mesagisto Project](https://github.com/MeowCat-Studio/mesagisto), the Discord implementation of the message forwarding client.**

## Deploy instruction

  1. Get binary file(shortly called tmf) at [Release page](https://github.com/MeowCat-Studio/discord-mesaga-fonto/releases) .
  2. Place it in where network access to Discord server is stable (you may need HTTP proxy).
  3. Run dmf, which automatically generates the default configuration file `config.yml`
  4. Use your favorite editor to edit `config.yml`。
 here is an example:
 ```yaml
---
# before use, set `enabled` to `true`.
enabled: true
forwarding:
  address: "nats://itsusinn.site:4222"
discord:
  token: "Nzk1Mjk0NawdWDA7wgyMjQ2.qwq.wdawd1J2OsJ4todWSGE_c07Cw"
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
 4. Start the dmf:
 ```shell
 # Giving executable permissions
 $ chmod +x ./dmf
 $ ./dmf
  INFO  discord_mesaga_fonto::message > Connecting to nats server
  INFO  discord_mesaga_fonto::message > Connected sucessfully
  INFO  discord_mesaga_fonto::event   > Bot:{{ BotName }} is connected!
 # To close dmf, use Ctrl+C
 $ ^C
  INFO  discord_mesaga_fonto          > Mesagisto Bot is shutting down
  INFO  discord_mesaga_fonto          > Saving configuration file
 ```
 5. Add the bot to the DC server, create a Discord channel, and enter the command in the channel:

 `/channel set <channel>`

> If you are using mirai source, the value of channel should be qq number
>
> In fact, whatever the value of the channel is, just make sure the value is the same for different forwarding clients
