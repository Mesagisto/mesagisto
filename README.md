# 消息转发 ![Icon](img/mail-send-line.svg)

![Github Action](https://github.com/itsusinn/message-forward/workflows/build/badge.svg)
**[ENGLISH](README.en-US.md)**|简体中文

本项目致力于通过消息转发连接不同的IM(Instant Messageing即时通讯)平台。

现已支持： Ｍinecraft我的世界， TencentQQ腾讯QQ，Discord等IM平台的互相转发。

## 安装 | 部署

**在Minecraft-Bukkit端部署**

1. 在[Releases页面](./releases) 下载bukkit-source.jar。

2. 移动至bukkit系服务端plugins文件夹下。

3. 启动一次服务器。

4. 修改plugins/MessageForward/config.yml，

   startSignal改为0，

   address改为 test_app_id.[你的qq号] (例如test_app_id.123456)。
5. 保存配置文件，重启bukkit服务端。
   

**在QQ(mirai)端部署**

1. 安装
   - 部署在安卓8.0+上(推荐)
     1. 安装[MiraiAndroid](https://github.com/mzdluo123/MiraiAndroid)
     2. 在[Releases页面](releases)下载mirai-android-source.apk。
     3. 将其作为安卓应用安装
     4. 安装前置插件[chat-command](https://github.com/project-mirai/chat-command)(apk形式，安装方法同上)。
   - 部署在服务器上
     1. 在[Releases页面](releases)下载mirai-source.jar。
     2. 移动至mirai-console([安装console?](https://github.com/iTXTech/mirai-console-loader))的plugins文件夹下。
     3. 安装前置插件[chat-command](https://github.com/project-mirai/chat-command)(jar形式，安装方法同上)。
2. 配置
   1. 登录一个QQ机器人
   2. 在私人聊天环境下输入/f ac test_app_id.[QQ号] test_token (注意空格)
   3. 在需要转发的QQ群内输入/f sc test_app_id.[QQ号] (仅管理员及以上可执行)

**在Discord端部署**
TODO

**在Telegram端部署**
TODO

**部署消息派发中心**
TODO

## 架构

```
message-source　消息源-IM的抽象概念
---bukkit-source　Minecraft聊天公屏
---mirai-source　QQ聊天
---discord-source　Discord文字频道

forward-client 转发客户端，封装心跳机制等
---vertx-client vertx实现
---okhttp-client okhttp实现

message-dispatcher　消息派发中心
```

## 维护者

[@Itsusinn](https://github.com/Itsusinn).

## 贡献

欢迎PR

## 许可证

LGPL v2.1 © Itsusinn
特例 [mirai-source AGPL v3](./message-source/mirai-source)
