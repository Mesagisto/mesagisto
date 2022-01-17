# mirai消息源
**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的mirai(Tencent-QQ)实现。**

## 安装
  1. 在[Releases页面](https://github.com/MeowCat-Studio/mirai-message-source/releases) 下载对应平台的 mms.jar。
  2. 移动至mirai-console(或是mcl)同目录的plugins文件夹下。
  > 注意安装前置插件chat-command, 详见本文档底端的注意事项
## 简单入门
 1. 运行一次MCL并关闭
 2. MCL目录下 找到配置文件 'config/Mesagisto/mesagisto.yml' 并修改
 参考:
 ```yaml
 # 中间转发服务器,消息的桥梁. 默认为我个人提供的[NATS](https://github.com/nats-io/nats-server)服务器
 nats:
   address: 'nats://itsusinn.site:4222'
# 加密设置
 cipher:
   # 是否启用加密
   enable: false
   # 加密用使用的密钥
   key: your-key
   # 是否拒绝未经加密的消息
   refuse-plain: true
 # 存放信使频道与QQ群的对应关系,默认为空. 不推荐手动添加.
 targetChannelMapper: {}
 ```

 3. 在 MCL控制台执行以下指令:
   `> /perm permit * org.meowcat.mesagisto:*`

  接着任何管理员或群主 (**除了BOT自身**) 可以执行以下指令
 ```shell
 /forward setChannel
 简洁形式: /f sc
 /forward setChannel <channel>
 简洁形式: /f sc <channel>
 # 此处channel的值为应设置的信使频道
 # 无论channel的值如何，只要保证不同转发客户端的值相同即可
 ```
 > 在 **QQ 群聊**(而不是 Mirai 控制台)中发送这些指令.
 >
 > 不给出`channel` 参数时,将使用默认值---命令发送者的QQ号

## 注意事项

  1. 现在信使**不再依赖**前置插件[chat-command]


  2. 若在Bot运行期间将普通成员提权至管理员,请重启Bot以刷新缓存

