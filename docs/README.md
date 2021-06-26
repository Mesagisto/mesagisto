# [Mesagisto](https://github.com/MeowCat-Studio/mesagisto) 信使 (former known as message-deliver)
[![QQ Group](https://img.shields.io/badge/QQ%20Group-667352043-12B7F5?logo=tencent-qq)](https://jq.qq.com/?_wv=1027&k=6eDIHSYt)
[![Telegram](https://img.shields.io/badge/Telegram-Ｍesagisto-blue.svg?logo=telegram)](https://t.me/mesagisto)

This project aim to transport messages between different IM platforms.

Now supporting: Minecraft([Bukkit](https://github.com/MeowCat-Studio/bukkit-mesaga-fonto)), [QQ](https://github.com/MeowCat-Studio/mirai-mesaga-fonto), Telegram, Discord

## How does it works

![graph](https://raw.fastgit.org/Itsusinn/draw-io/master/message-forwarding/architecture.svg)

TODO

## Usage

TL,DR:
 - deploy Mirai using [mcl](https://github.com/iTXTech/mirai-console-loader),
 - install plugins:
   - [chat-command](https://github.com/project-mirai/chat-command/releases)
   - something you like from the subrepositories
 - after install them, grant permissions to plugins by executing commands below in `mcl`:
   > /perm permit * net.mamoe.mirai.console.chat-command:*
   > ...
 

>How to install a plugin:
> In MCL 1.0.5, simply drop the '.jar' file that released into the `~/plugins/` directory, which '~' represents the directory which 'mcl.jar' placed. 
> You may also try `./mcl --update-package` method, see also [README](https://hub.fastgit.org/iTXTech/mirai-console-loader/blob/master/scripts/README.md), [UserManual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
___
## Contributors

[@Itsusinn](https://github.com/Itsusinn).
Any PR would be welcomed.
___

