# Telegram message source

** The function of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto) is to forward messages to the telegram client **

## Requirement

1. Bot should set group privacy mode to off at botfather, otherwise your bot will not be able to access group chat messages

## Deploy

1. Get binary files on the [Release Page](https://github.com/MeowCat-Studio/telegram-message-source/releases)  (hereinafter referred to as TMS)
!!! Note

     File naming rules: TG-<schema>-<operating system>-<features>
     Binary for Windows users, the executable file will have a colored suffix. The colored version of the file has the color code of the terminal, and there may be garbled code in PS (PowerShell).
     It is recommended that Windows users with MinGW terminal download this version

1. Ensure that TMS can access the telegram server in a stable network environment (HTTP proxy may be required, see the configuration file section of this document for details)

2. Run TMS and automatically generate the default configuration file `config/tg.yml`

3. Edit configuration file `config/tg.yml`
```yaml
# Change 'enable' to 'true' before use.
enable: true
# Intermediate forwarding server, message bridge.
# The default is Mesagisto commonweal [Nats](https://github.com/nats-io/nats-server) Server
nats:
  address: "nats://nats.mesagisto.org:4222"
# Encryption settings
cipher:
  # Key used for encryption
  key: test
telegram:
  # Token key of TGBot, obtained from @botfather
  token: "114514191:IYokoiYoT4YfU_NA9NzhS5HS5oT-oJTrE"
proxy:
  # Enable agent
  enable: true
  # Only HTTP proxy is allowed at this stage (reqwest library limit)
  address: "http://127.0.0.1:7890"
# Stores the correspondence between Mesagisto channel and TG group. It is empty by default Manual addition is not recommended.
bindings: {}
```

5. Start TMS:
```shell
# Give executable permission
$ chmod +x ./tms
# Run
$ ./tms
# To turn off TMS, please use Ctrl + C. don't turn it off smoothly.
$ ^C
```
If there is no [error] output, you can send `/help` to the Bot, and you will get the following reply:
```
Mesagisto BOT supports the following commands:

/about — About this project
/unbind — Unbind the forwarding address of the current group
/help — Display command help
/status — Display status
/bind — Bind the forwarding address of the current group
```

6. Create a telegram group, add BOT to the group, and input instructions in the group:
`/bind <channel>`

## Matters needing attention

1. No matter what the value of channel is, as long as the channels bound by each forwarding client are the same
2. After changing the group privacy mode halfway, please remove the BOT from the group and restart it