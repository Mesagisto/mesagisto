
# bungee消息源

** [Mesagisto 信使](https://github.com/MeowCat-Studio/mesagisto) 的功能实现，功能为转发消息到 Minecraft[Bungee] 客户端 **


## 需求

- 对于Windows, 需要安装 [Microsoft Visual C++ 2010 Redistributable运行时](https://www.microsoft.com/en-us/download/details.aspx?id=26999) 运行时位数应与JDK保持一致

## 安装

1. 在[Releases页面](https://github.com/MeowCat-Studio/bungee-message-source/releases) 下载 jar 归档文件

2. 将jar包移动至 bungeecord 系服务端(如 Waterfall Velocity 等)的plugins文件夹下

3. 启动服务器,此时会自动生成配置文件

4. 在确保** 服务器关闭 **的情况下，修改 `plugins/mesagisto/config.yml`

```yaml
# 是否启用信使
enable: true
# 您的信使频道绑定
# 可手动编辑,但建议通过指令添加
bindings:
  # 服务器名: 信使频道
  sub1: "test"
  sub2: "test"
# 加密设置
cipher:
  # 加密用使用的密钥 {==需确保转发各端相同==}
  key: "default"
# 中间转发服务器,消息的桥梁.
# 默认为信使公益[NATS](https://github.com/nats-io/nats-server)服务器
nats: "nats://nats.mesagisto.org:4222"
# 消息模板
template:
  message: "§7<{{sender}}> {{content}}"
```
5. 保存配置文件，启动服务器。

6. 在需要设置信使的子服内使用 `/msgist help` 查看帮助 使用 `/msgist [频道名]` 绑定信使频道


## 注意事项

1. 需要转发的消息源需要绑定同一个 ** 频道名**
2. 权限管理,本插件的权限节点为 `mesagisto` , 若想使用信使的命令需要授予该权限

=== "不使用权限管理插件"

    修改bungee配置文件config.yml为
    ```yaml
    permissions:
      admin:
      - some-other-perm
      - {++mesagisto++}
    ......
    groups:
      playername:
      - default
      - {++admin++}
    ```
=== "LuckPerms"

    在拥有 LuckPerms 的服务器 Console 中执行命令
    ```
    lpb user 玩家名 permission set mesagisto
    ```
