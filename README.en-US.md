# Mesagisto 信使
[![QQ Group](https://img.shields.io/badge/QQ%20Group-667352043-12B7F5?logo=tencent-qq)](https://jq.qq.com/?_wv=1027&k=6eDIHSYt)
[![Telegram](https://img.shields.io/badge/Telegram-Ｍesagisto-blue.svg?logo=telegram)](https://t.me/mesagisto)

ENGLISH | **[简体中文](https://github.com/MeowCat-Studio/mesagisto/blob/master/README.zh-CN.md)**

This project aims to transport messages between different IM platforms.

> Imagine a world...
>
> ...where it is as simple to message or call anyone as it is to send them an email.
>
> ...where you can communicate without being forced to install the same app.

## How does it work

This project assumes that different message sources are distributed on networked computers, so different IM-related components need to communicate and coordinate their actions through messaging.

This project is therefore built on top of [NATS](https://nats.io/), an open source, lightweight, high-performance distributed messaging middleware. Different chat platforms communicate through NATS. 


## Features

- **High Performance**: Uses coroutines (aka green threads), and on the JVM we aggressively use platform-related JNI libraries.
- **Asynchronous**: Does not block the main thread on Minecraft related platforms and implements structured concurrency as much as possible. Almost no blocking IO is used.
- **Low Overhead**: Try to use GC-free Rust to write related components and JVM to use off-heap memory with the help of JNI.
- **Automation**: The project uses [GH-Action](https://github.com/features/actions) for continuous integration and focuses on a highly reproducible build process. New changes to the application code are regularly built, tested and released to GH-Release.

## Install & Usage

See  **[User Guide](https://docs.mesagisto.org/zh-CN/)**

## Progress

### Message type

- [x] Text message
- [x] Image message
- [ ] Video message
- [ ] File message
- [ ] Voice message
- [x] Reply to quote
- [ ] Mention(@)
- [ ] Merged forward message

### Chat type

- [x] Group chat
- [ ] Private chat
- [ ] Log pull 
- [ ] TG Channel Post
- [x] TG channel discussion forum

### Message source
- [x] [QQ-MiraiConsole](https://github.com/MeowCat-Studio/mirai-message-source) via [mirai](https://github.com/mamoe/mirai) [![build](https://github.com/MeowCat-Studio/mirai-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/mirai-message-source/actions/workflows/build.yml)
- [ ] [QQ-OneBot12](https://github.com/MeowCat-Studio/onebot-message-source) via [walle-core](https://github.com/abrahum/Walle-core) *Delayed*
- [x] [Telegram](https://github.com/MeowCat-Studio/telegram-message-source) via [teloxide](https://github.com/teloxide/teloxide) [![build](https://github.com/MeowCat-Studio/telegram-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/telegram-message-source/actions/workflows/build.yml)
- [x] [Discord](https://github.com/MeowCat-Studio/discord-message-source) via [serenity](https://github.com/serenity-rs/serenity) [![build](https://github.com/MeowCat-Studio/discord-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/discord-message-source/actions/workflows/build.yml)
- [x] [Minecraft-Bukkit](https://github.com/MeowCat-Studio/kato-message-source) 1.12-1.19 [![build](https://github.com/MeowCat-Studio/kato-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/kato-message-source/actions/workflows/build.yml)
- [x] [Minecraft-Fabric](https://github.com/MeowCat-Studio/fabric-message-source) 1.16-1.18 [![build](https://github.com/MeowCat-Studio/fabric-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/fabric-message-source/actions/workflows/build.yml)
- [x] [Minecraft-Forge](https://github.com/MeowCat-Studio/forge-message-source) 1.18 [![build](https://github.com/MeowCat-Studio/forge-message-source/actions/workflows/build.yml/badge.svg)](https://github.com/MeowCat-Studio/forge-message-source/actions/workflows/build.yml)
- [ ] [Minecraft-Bungeecord/Velocity](https://github.com/MeowCat-Studio/bungeecord-message-source) *WorkInProgress*
- [ ] [Matrix-Oicq-Bridge](https://github.com/Mesagisto/matrix-oicq-bridge)  via [Matrix](https://matrix.org/) *WorkInProgress*


## Contributors

[@Itsusinn/逸新](https://github.com/Itsusinn)(Maintainer)

[@nexplorer-3e](https://github.com/nexplorer-3e)

[@MlgmXyysd](https://github.com/MlgmXyysd)

[@qwq233](https://github.com/qwq233)

[@DreamOneX](https://github.com/DreamOneX)

[@LuDreamst](https://github.com/LuDreamst)

Any PR would be welcomed.

___

