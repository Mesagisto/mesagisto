# bukkit消息源
**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的bukkit(Minecraft)实现。**

## 安装

1. 在[Releases页面](https://github.com/MeowCat-Studio/bukkit-message-source/releases) 
  下载相应OS及CPU架构的jar归档文件。
  > 文件命名规则：kato-<架构>-<操作系统>.jar

2. 将jar包移动至bukkit系服务端(如Spigot,Paper等)的plugins文件夹下。

3. 启动服务器,此时会自动生成配置文件。

4. 修改plugins/mesagisto/config.yml，
  参考
  ```yaml
  # 是否启用信使
  enable: true
  # 您的信使频道, 无论channel的值如何，
  # 只要保证不同转发客户端channel的值相同即可
  channel: test
  id-base: 30
  # 中间转发服务器,消息的桥梁.
  # 默认为我个人提供的[NATS](https://github.com/nats-io/nats-server)服务器
  nats:
    address: nats://itsusinn.site:4222
  # 加密设置
  cipher:
    # 是否启用加密
    enable: true
    # 加密用使用的密钥 需保证各端相同
    key: my-key
    # 是否拒绝未经加密的消息
    refuse-plain: true
  ```

5. 保存配置文件，重启bukkit服务端。

## 注意事项
1. 与InteractiveChat的兼容性问题,请编辑plugins/InteractiveChat/config.yml
  找到
  ```yaml
  Settings:
    Bungeecord: false
    ChatListeningPlugins:
      - "Plugin:QuickShop, Class:.*, EventPriority:LOWEST"
      - "Plugin:Slimefun, Class:.*, EventPriority:LOWEST"
  ```
  在列表ChatListeningPlugins中添加`"Plugin:bukkit-message-source, Class:.*, EventPriority:NORMAL"`即
  ```yaml
  Settings:
    Bungeecord: false
    ChatListeningPlugins:
      - "Plugin:QuickShop, Class:.*, EventPriority:LOWEST"
      - "Plugin:Slimefun, Class:.*, EventPriority:LOWEST"
      - "Plugin:bukkit-message-source, Class:.*, EventPriority:NORMAL"
  ```

