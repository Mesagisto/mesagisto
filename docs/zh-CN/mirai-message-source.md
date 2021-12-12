# mirai消息源
**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的mirai(Tencent-QQ)实现。**

## 安装
  1. 在[Releases页面](https://github.com/MeowCat-Studio/mirai-message-source/releases) 下载对应平台的 mms.jar。
  2. 移动至mirai-console(或是mcl)同目录的plugins文件夹下。
  > 注意安装前置插件chat-command, 详见本文档底端的注意事项
## 简单入门

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

  1. 为了在聊天环境中使用命令需要安装**前置插件[chat-command](https://github.com/project-mirai/chat-command)**

  > 安装完chat-command后，须在mirai控制台输入指令
  >
  > `permission permit * net.mamoe.mirai.console.chat-command:*`

  2. 若在Bot运行期间将普通成员提权至管理员,请重启Bot以刷新缓存

