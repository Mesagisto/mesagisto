# mirai-mesaga-fonto 
** part of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto). A implementation of message-forwarding client.**

## Install

  - Installation on the server

    1. Download mmf.mirai.jar from [Releases page](https://github.com/MeowCat-Studio/mirai-mesaga-fonto/releases).
    2. Move to the plugins folder of mirai-console (or mcl).

  - Install on Android (8.0+)
    1. Download mamf.apk from [Releases page](https://github.com/MeowCat-Studio/mirai-mesaga-fonto/releases).
    2. Install it as an Android application

## Simple instruction

__You may take a look at main repo's readme for usage first.__

Execute the command below in MCL:

   `> /perm permit * org.meowcat.mesagisto:*`

Then any OP (EXCEPT bot itself) can execute commands below
 ```
 /forward setChannel
 or shortly: /f sc
 /forward setChannel <channel>
 or shortly: /f sc <channel>
 ```
by simply send them in a **QQ group chat**(not Mirai Console).

If no `channel` parameter is given, the default value will be used,which is the command sender's qq number.


## Attention

In order to use commands in the chat environment you need to install the **preceding plugin [chat-command](https://github.com/project-mirai/chat-command)**

> After installing the chat-command, you must enter the command in the mirai console
>
> `permission permit * net.mamoe.mirai.console.chat-command:*`

