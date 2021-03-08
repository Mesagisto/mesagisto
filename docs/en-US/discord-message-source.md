# Discord message source

** The function of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto) is to forward messages to discord client **

## Requirement

- When registering Bot in discord developer portal, please go to the Bot page on the left and turn on presence intent, server members intent and message content intent under privileged gateway intentions

## Deploy-
1. Get the binary file (dms for short) on the [Release Page](https://github.com/MeowCat-Studio/discord-message-source/releases)
!!! Note
     File naming rules: dc-<schema>-<operating system>-<features>
     Binary for Windows users, the executable file will have a colored suffix. The colored version of the file has the color code of the terminal, and there may be garbled code in PS (PowerShell).
     It is recommended that Windows users with MinGW terminal download this version

2. Ensure that the dms is in a network environment that can stably access the discord server (HTTP proxy may be required, see the configuration file section of this document for details)

3. Run dms and automatically generate the default configuration file `config/dc.yml`

4. Edit configuration file `config/dc.yml`
  ```yaml
  # Change 'enable' to 'true' before use
  enable: true
  # Bot's key can be obtained from discord developer portal
  discord:
    token: OTMxNTA1MTU0NjY1MDkxMTEz.YeFZxw.YzOVoAFsW3joO9VX5sTMhhGsoXo
  # Network proxy settings
  proxy:
    # Enable agent
    enabled: true
    # Only HTTP proxy is allowed at this stage (request library limit)
    address: "http://127.0.0.1:7890"
  # Intermediate forwarding server, message bridge
  # The default is mesagisto commonweal [Nats](https://github.com/nats-io/nats-server) Server
  nats:
    address: "nats://nats.mesagisto.org:4222"
  # Encryption settings
  cipher:
    # Key used for encryption
    key: test

  # Stores the correspondence between mesagisto channel and DC text channel. It is empty by default Manual addition is not recommended
  bindings: {}
  ```
5. Start dms:
  ```shell
  # Give executable permission
  $ chmod +x ./tms
  # Run
  $ ./dms
  # To close dms, please use Ctrl + C, and do not close it smoothly
  $ ^C
  ```

6. Add your BOT to the DC server, create a discord text channel, and enter instructions in the text channel: `/help`
You will get the help of instructions
Use `/channel bind <channel>` to set the mesagisto channel

## Matters needing attention

1. No matter what the value of channel is, as long as the channels bound by each forwarding client are the same
