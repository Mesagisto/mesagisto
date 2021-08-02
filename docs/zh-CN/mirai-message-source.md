# mirai消息源
**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的mirai(Tencent-QQ)实现。**

## 安装

  - 安装在服务器上

    1. 在[Releases页面](https://github.com/MeowCat-Studio/mirai-mesaga-fonto/releases) 下载 mmf.mirai.jar。
    2. 移动至mirai-console(或是mcl)的plugins文件夹下。

  - 安装在安卓(8.0+)上
    1. 在[Releases页面](https://github.com/MeowCat-Studio/mirai-mesaga-fonto/releases) 下载 mamf.apk。
    2. 将其作为安卓应用安装
## 简单入门

__您可以先看看主程序库的使用说明。__

 在 MCL控制台执行以下指令:

   `> /perm permit * org.meowcat.mesagisto:*`

接着任何管理员或群主 (**除了BOT自身**) 可以执行以下指令
 ```
 /forward setChannel
 简洁形式: /f sc
 /forward setChannel <channel>
 简洁形式: /f sc <channel>
 ```
在 **QQ 群聊**(而不是 Mirai 控制台)中发送这些指令.

不给出`channel` 参数时,将使用默认值---命令发送者的QQ号

## 注意事项

为了在聊天环境中使用命令需要安装**前置插件[chat-command](https://github.com/project-mirai/chat-command)**

> 安装完chat-command后，须在mirai控制台输入指令
>
> `permission permit * net.mamoe.mirai.console.chat-command:*`

