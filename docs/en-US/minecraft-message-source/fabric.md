# Fabric消息源
**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的fabric(Minecraft)实现。**

## 安装

1. 在[Releases页面](https://github.com/MeowCat-Studio/fabric-message-source/releases)
  下载相应OS及CPU架构的jar归档文件。
  > 文件命名规则：fabric-<MC版本>-<架构>-<操作系统>.jar

2. 将jar包移动至fabric服务端的mods文件夹下。

3. 启动服务器,此时会自动生成配置文件, 关闭服务器。

4. 修改mods/mesagisto/config.yml，
  参考
  ```yaml
  # 是否启用信使
  enable: true
  # 您的信使频道, 无论channel的值如何，
  # 只要保证不同转发客户端channel的值相同即可
  channel: test
  id-base: 0
  # 中间转发服务器,消息的桥梁.
  # 默认为我个人提供的[NATS](https://github.com/nats-io/nats-server)服务器
  nats:
    address: nats://itsusinn.site:4222
  # 加密设置
  cipher:
    # 加密用使用的密钥 需保证各端相同
    key: my-key
  ```

5. 启动fabric服务端。

## 注意事项
1. 需要前置依赖 Fabric-API 任意版本
2. 对于Windows, 需要安装 [Microsoft Visual C++ 2010 Redistributable运行时](https://www.microsoft.com/en-us/download/details.aspx?id=26999)
