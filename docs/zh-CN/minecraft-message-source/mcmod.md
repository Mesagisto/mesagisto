# MCMod消息源

** [Mesagisto 信使](https://github.com/MeowCat-Studio/mesagisto) 的功能实现，功能为转发消息到 Minecraft[Mod] 客户端 **

## 需求

=== "Fabric 1.16-1.19"

	- 前置依赖 [Fabric-API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) 任意版本
	- 前置依赖 [fabric-language-kotlin](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin) 最新版

=== "Forge1.18"

	- 前置依赖 [KotlinForForge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge)

## 安装

1. 在 [Releases页面](https://github.com/Mesagisto/mcmod-message-source/releases) 下载 jar 归档文件
2. 将 jar 移动至 fabric/forge 服务端的 mods 文件夹下
3. 启动服务器,此时会自动生成配置文件
4. 修改 `mods/mesagisto/config.yml`
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
    key: default-key
  ```
5. 重启 fabric/forge 服务端。

## 注意事项

1. 无论channel的值如何，只要保证各个转发客户端绑定的频道相同即可
