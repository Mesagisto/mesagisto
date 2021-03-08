# Bukkit 消息源

** [Mesagisto 信使](https://github.com/MeowCat-Studio/mesagisto) 的功能实现，功能为转发消息到 Minecraft[Bukkit] 客户端 **

## 安装

1. 在[Releases页面](https://github.com/MeowCat-Studio/bukkit-message-source/releases) 下载 jar 归档文件

2. 将jar包移动至bukkit系服务端(如Spigot,Paper等)的 plugins 文件夹下

3. 启动服务器,此时会在 plugins 文件夹下自动生成配置文件

4. 修改 `plugins/mesagisto/config.yml`
  ```yaml
  # 是否启用信使
  enable: true
  # 您的信使频道, 无论channel的值如何，
  # 只要保证不同转发客户端channel的值相同即可
  channel: "your-channel"
  # 服务器的TargetName, 具有相同Target的群聊/服务器不会显示彼此的消息
  # 这对于那些安装了子服间消息互通的服务器可能很有用
  target: "target-name"
  # 加密设置
  cipher:
    # 加密用使用的密钥 需保证各端相同
    key: "your-key"
  ```

5. 保存配置文件，重启bukkit服务端

## 注意事项

1. 避免使用热重载, 如果出现问题请先手动重启
2. 无论 channel 的值如何，只要保证各个转发客户端绑定的频道相同即可
