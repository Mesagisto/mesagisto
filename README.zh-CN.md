# Mesagisto信使
[![QQ Group](https://img.shields.io/badge/QQ%20Group-667352043-12B7F5?logo=tencent-qq)](https://jq.qq.com/?_wv=1027&k=6eDIHSYt)
[![Telegram](https://img.shields.io/badge/Telegram-Ｍesagisto-blue.svg?logo=telegram)](https://t.me/mesagisto)

**[ENGLISH](https://github.com/MeowCat-Studio/mesagisto/blob/master/README.en-US.md)** | 简体中文

本项目致力于通过消息转发连接不同的即时通讯平台。

> 想象一个世界...
> ...在那里给任何人发信息或打电话就像给他们发电子邮件一样简单。
> ...在那里，你可以沟通，而不需要被迫安装同一个应用程序。

## 它是如何工作的？

本项目假定不同消息源分布在网络计算机上，因此不同的IM相关组件需要通过消息传递来通信并协调行动.

因此本项目建立在[NATS](https://nats.io/),  一个开源、轻量级、高性能的分布式消息中间件之上. 不同的聊天平台通过NATS通信. 

## 特性

- **高性能**:  使用协程(又名绿色线程)，在JVM上我们激进地使用平台相关的JNI库。
- **异步**: 在Minecraft相关平台上不阻塞主线程，并尽量实现结构化并发. 几乎不使用阻塞式IO。
- **低开销**: 尽量使用无GC的Rust编写相关组件，JVM在JNI的帮助之下使用堆外内存。
- **自动化**: 本项目使用[GH-Action](https://github.com/features/actions)进行持续集成，并注重高可复现的构建流程. 应用代码的新更改会定期构建、测试并发布到到GH-Release中。

## 我该如何使用安装并使用它？

请查看 [用户指南](https://github.com/MeowCat-Studio/mesagisto-docs/blob/master/zh-CN/intro.md)

## 进度

### 消息类型

- [x] 文本消息
- [x] 图片消息
- [ ] 视频消息
- [ ] 文件消息
- [ ] 语音消息
- [x] 回复引用
- [ ] Mention(@)
- [ ] 合并转发

### 聊天类型

- [x] 群聊
- [ ] 私聊
- [ ] 日志拉取 
- [ ] TG频道Post
- [x] TG频道讨论区

### 消息源
- [x] [QQ-MiraiConsole](https://github.com/MeowCat-Studio/mirai-message-source) via [mirai](https://github.com/mamoe/mirai) [![build](https://github.com/MeowCat-Studio/mirai-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/mirai-message-source/actions/workflows/build.yml)
- [ ] [QQ-OneBot12](https://github.com/MeowCat-Studio/onebot-message-source) via [walle-core](https://github.com/abrahum/Walle-core) *Delayed*
- [x] [Telegram](https://github.com/MeowCat-Studio/telegram-message-source) via [teloxide](https://github.com/teloxide/teloxide) [![build](https://github.com/MeowCat-Studio/telegram-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/telegram-message-source/actions/workflows/build.yml)
- [x] [Discord](https://github.com/MeowCat-Studio/discord-message-source) via [serenity](https://github.com/serenity-rs/serenity) [![build](https://github.com/MeowCat-Studio/discord-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/discord-message-source/actions/workflows/build.yml)
- [x] [Minecraft-Bukkit](https://github.com/MeowCat-Studio/kato-message-source) 1.12-1.19 [![build](https://github.com/MeowCat-Studio/kato-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/kato-message-source/actions/workflows/build.yml)
- [x] [Minecraft-Fabric](https://github.com/MeowCat-Studio/fabric-message-source) 1.16-1.18 [![build](https://github.com/MeowCat-Studio/fabric-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/fabric-message-source/actions/workflows/build.yml)
- [x] [Minecraft-Forge](https://github.com/MeowCat-Studio/forge-message-source) 1.18 [![build](https://github.com/MeowCat-Studio/forge-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/forge-message-source/actions/workflows/build.yml)
- [ ] [Minecraft-Bungeecord/Velocity](https://github.com/MeowCat-Studio/bungeecord-message-source) *WorkInProgress*
- [ ] [Matrix-Oicq-Bridge](https://github.com/Mesagisto/matrix-oicq-bridge)  via [Matrix](https://matrix.org/) *WorkInProgress*


## 贡献者

[@Itsusinn/逸新](https://github.com/Itsusinn)(Maintainer)

[@nexplorer-3e](https://github.com/nexplorer-3e)

[@MlgmXyysd](https://github.com/MlgmXyysd)

[@qwq233](https://github.com/qwq233)

[@DreamOneX](https://github.com/DreamOneX)

[@LuDreamst](https://github.com/LuDreamst)

欢迎任何形式的PR。

___

