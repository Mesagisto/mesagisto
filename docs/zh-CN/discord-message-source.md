# Discord消息源

**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的Discord 实现。**

## 部署

 1. 在 [Release页面](https://github.com/MeowCat-Studio/discord-message-source/releases)获取二进制文件(简称dms)。

 2. 确保dms在能稳定访问访问Discord服务器的网络环境下（可能需要HTTP代理,详见本文档配置文件部分）。

 3. 运行dms,自动生成默认配置文件`config/dc.yml`

 4. 编辑配置文件`config/dc.yml`。

   示例:
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
    # 现阶段仅允许http代理(reqwest库限制)
    address: "http://127.0.0.1:7890"
  # 中间转发服务器,消息的桥梁. 默认为我个人提供的[NATS](https://github.com/nats-io/nats-server)服务器
  nats:
    address: "nats://itsusinn.site:4222"
  # 加密设置
  cipher:
    # 是否启用加密
    enable: true
    # 加密用使用的密钥
    key: test
    # 是否拒绝未经加密的消息
    refuse_plain: true

  # 存放信使频道与DC文字频道的对应关系,默认为空. 不推荐手动添加.
  target_address_mapper: {}
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
  6. 将bot添加至DC服务器，创建一个 Discord 文字频道，并在文字频道内输入指令:`/help`
  你将获得指令的帮助 <br/><br/>
  使用`/channel set <channel>`来设置信使频道
  > 无论channel的值如何，只要保证不同转发客户端的值相同即可

## 注意事项

1. 在Discord Developer Portal注册Bot时, 请前往左侧Bot分页, 将Privileged Gateway Intents的Presence Intent,Server Members Intent,Message Content Intent全部开启
