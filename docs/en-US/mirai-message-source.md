# Mirai Message source

** Mirai is used for the function implementation of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto), and the function is to forward messages to the client (Tencent QQ)**


## Requirement

- Mirai 2.12.0+, MCL 2.1.0+

- Front plugin [ChatCommand](https://github.com/project-mirai/chat-command)

- For Windows, [Microsoft Visual C + + 2010 redistributable](https://www.microsoft.com/en-us/download/details.aspx?id=26999) needs to be installed. The number of bits of runtime should be consistent with JDK


!!! Warning
    Mesagisto will automatically configure command permissions, please do not use permission command to operate Mesagisto permissions (because it will be reset every time you start)
## Installation

=== "Manual installations"

	Download the jar archive on the [releases page](https://github.com/MeowCat-Studio/mirai-message-source/releases). Move to the plugins folder in the same directory of Mirai console (MCL).

=== "MCL auto install - stable"

	Using the MCL command `./mcl --update-package org.mesagisto:mirai-message-source --channel stable --type plugin`

	Use every time you start `./mcl -u` to update
=== "MCL auto install - pre"

	Using the MCL command `./mcl --update-package org.mesagisto:mirai-message-source --channel pre-release --type plugin`

	Use every time you start `./mcl -u` to update

  Note: since the pre release version is only released on GitHub release, [GH-Proxy](https://ghproxy.com/) is used


  However, access is still blocked in some regions. It is recommended to modify the MCL configuration file `config.json` Proxy options
## Simple introduction

1. Run MCL once and close it

2. Find the configuration file config/org.mesagisto.mirai-message-source/config.yml and modify
```yaml
# Intermediate forwarding server, message bridge.
# The default is Mesagisto commonweal [NATs](https://github.com/nats-io/nats-server) Server
nats:
  address: 'nats://nats.mesagisto.org:4222'
# Encryption settings
cipher:
  # Key used for encryption
  key: your-key
# Network agent, required for downloading telegram or discord pictures
# Note that if TGBot / DCBot and MiraiBot are on the same host
# Just set the agent of TGBot / DCBot
proxy:
  # Enable agent
  enable: false
  # Proxy server address
  address: 'http://127.0.0.1:7890'
# Experimental options
perm: 
  # Strict mode, when enabled
  # Mesagisto only responds to commands sent by users in the users list below
  # When disabled, the Mesagisto will respond to the instructions sent by all users
  # However, channel binding only allows administrators to operate
  strict: false
  # User list, QQ number
  users: 
    - 123456
# Stores the correspondence between Mesagisto channel and QQ group. It is empty by default Manual addition is not recommended
bindings: {}
```

3. In ** QQ group chat ** (instead of Mirai console), you can execute the following command ` /msgist` or `/信使`. You will get
```text
    The parameters do not match. Do you want to execute: 
    /msgist about    (insufficient parameters)
    /msgist ban <user>    (insufficient parameters)
    /msgist bind <channel>    (insufficient parameters)
    /msgist disable <group/channel>    (insufficient parameters)
    /msgist enable <group/channel>    (insufficient parameters)
    /msgist status    (insufficient parameters)
    /msgist unban <user>    (insufficient parameters)
    /msgist unbind    (insufficient parameters)
```
4. Use `/msgist bind channel `to bind to Mesagisto channel

## Matters needing attention
 1. The message source to be forwarded needs to be bound to the same ** channel name **
 2. The blacklist function needs to be in strict mode
