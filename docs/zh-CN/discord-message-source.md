# discord-mesaga-fonto 

**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的Discord实现。**

## 部署指导

 1. 在 [Release页面](https://github.com/MeowCat-Studio/discord-mesaga-fonto/releases)获取二进制文件(简称dmf)。

  2. 将tmf放在网络访问Discord服务器稳定的地方（你可能需要HTTP代理）。
  3. 运行tmf,自动生成默认配置文件`config.yml`
  4. 用你喜欢的编辑器编辑`config.yml`。

   示例:
 ```yaml
---
# 在使用前将 `enabled` 改为 `true`.
enabled: true
forwarding:
  address: "nats://itsusinn.site:4222"
discord:
  token: "Nzk1Mjk0NawdWDA7wgyMjQ2.qwq.wdawd1J2OsJ4todWSGE_c07Cw"
proxy:
  # 是否启用代理
  enabled: true
  # 现阶段仅允许http代理(reqwest库限制)
  address: "http://127.0.0.1:7890"
# 默认为空, 不推荐手动添加.
# 格式：
# "<chat_id>"= "<channel>" ("str"= "str")
# 例子：
# "-11451419" = "10000"
target_address_mapper: {}

 ```
 4. 启动dmf:
 ```shell
 # 给予可执行权限
 $ chmod +x ./dmf
 $ ./dmf
  INFO  discord_mesaga_fonto::message > Connecting to nats server
  INFO  discord_mesaga_fonto::message > Connected sucessfully
  INFO  discord_mesaga_fonto::event   > Bot:{{ BotName }} is connected!
 # 若要关闭tmf,请使用Ctrl+C
 $ ^C
  INFO  discord_mesaga_fonto          > Mesagisto Bot is shutting down
  INFO  discord_mesaga_fonto          > Saving configuration file
 ```
 5. 将bot添加至DC服务器，创建一个 Discord 频道，并在频道内输入指令:

 `/channel set <channel>`

> 如果你在使用mirai消息源,channel的值应当是qq号
>
> 实际上无论channel的值如何，只要保证不同转发客户端的值相同即可

## 注意事项
  1. 在Discord Developer Portal注册Bot时, 请前往左侧Bot分页, 
    将Privileged Gateway Intents的Presence Intent,Server Members Intent,Message Content Intent全部开启
