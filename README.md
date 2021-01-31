Project Message-Forward

本项目致力于通过消息转发连接不同的IM(Instant Message即时通讯)平台

现已支持: Ｍinecraft我的世界, TencentQQ腾讯QQ,Discord等IM平台的互相转发

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

