# mirai-mesaga-fonto 
### part of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto)

---


A implementation of message-forwarding-client.
消息转发客户端的mirai(Tencent-QQ)实现

___

Install

TL,DR:
 - deploy Mirai using [mcl](https://github.com/iTXTech/mirai-console-loader),
 - install plugins:
   - [chat-command](https://github.com/project-mirai/chat-command/releases)
   - something you like from the subrepositories
 - after install them, grant permissions to plugins by executing commands below in `mcl`:
   > /perm permit * net.mamoe.mirai.console.chat-command:*
   > ...


>#### How to install a plugin:
>
>In MCL 1.0.5, simply drop the '.jar' file that released into the `~/plugins/` directory, which '~' represents the directory which 'mcl.jar' placed. 
>
>You may also try `./mcl --update-package` method, see also [README](https://hub.fastgit.org/iTXTech/mirai-console-loader/blob/master/scripts/README.md), [UserManual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)

___



## Simple instruction

__You may take a look at main repo's readme for usage first.__

To install, get .jar from Release page, drop it into MCL's `plugins/`, and execute command below in MCL:

   `> /perm permit * org.meowcat.mesagisto:*`

Then any OP (EXCEPT bot itself) can execute commands below
 ```
 /forward setChannel 
 /forward channel
 /forward channel <channel>
 ```
by simply send them in a **QQ group chat**(not Mirai Console).

`channel` represents QQ number here.



