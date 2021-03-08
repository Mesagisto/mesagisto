# Discord 消息源

** [Mesagisto 信使](https://github.com/MeowCat-Studio/mesagisto) 的功能实现，功能为转发消息到 Discord 客户端 **
## 需求

- 在Discord Developer Portal 注册 Bot 时, 请前往左侧 Bot 分页, 将 Privileged Gateway Intents 下的 Presence Intent , Server Members Intent 以及 Message Content Intent 全部开启

## 部署

1. 在 [Release页面](https://github.com/MeowCat-Studio/discord-message-source/releases) 获取二进制文件(以下简称dms)
!!! Note
     文件命名规则：dc-<架构>-<操作系统>-<特性>

     二进制对于 Windows 用户而言, 可执行文件会带有 colored 后缀，colored 版本的文件有终端的颜色代码，PS(PowerShell)下可能出现乱码。
     推荐有 MINGW 终端的 Winodws 用户下载该版本

2. 确保 dms 在能稳定访问访问 Discord 服务器的网络环境下（可能需要HTTP代理,详见本文档配置文件部分）

3. 运行 dms ,自动生成默认配置文件 `config/dc.yml`

4. 编辑配置文件 `config/dc.yml` 
  ```yaml
  ---
  # 在使用前将 `enable` 改为 `true`.
  enable: true
  # Bot的密钥,于Discord Developer Portal获得
  discord:
    token: OTMxNTA1MTU0NjY1MDkxMTEz.YeFZxw.YzOVoAFsW3joO9VX5sTMhhGsoXo
  # 网络代理设置
  proxy:
    # 是否启用代理
    enabled: true
    # 现阶段仅允许http代理(request库限制)
    address: "http://127.0.0.1:7890"
  # 中间转发服务器,消息的桥梁.
  # 默认为信使公益[NATS](https://github.com/nats-io/nats-server)服务器
  nats:
    address: "nats://nats.mesagisto.org:4222"
  # 加密设置
  cipher:
    # 加密用使用的密钥
    key: test

  # 存放信使频道与DC文字频道的对应关系,默认为空. 不推荐手动添加.
  bindings: {}
  ```
5. 启动dms:
  ```shell
  # 给予可执行权限
  $ chmod +x ./tms
  # 运行
  $ ./dms
  # 若要关闭dms,请使用Ctrl+C,切忌不平滑关闭
  $ ^C
  ```

6. 将你的 bot 添加至 DC 服务器，创建一个 Discord 文字频道，并在文字频道内输入指令: `/help`
你将获得指令的帮助
使用`/channel bind <channel>`来设置信使频道

## 注意事项

1. 无论 channel 的值如何，只要保证各个转发客户端绑定的频道相同即可
