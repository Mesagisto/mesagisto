# telegram-mesaga-fonto 

**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的[Telegram](https://core.telegram.org) 实现。**

## 部署指导

 1. 在 [Release页面](https://github.com/MeowCat-Studio/telegram-mesaga-fonto/releases)获取二进制文件(简称tmf)。

 2. 将tmf放在网络访问Telegram服务器稳定的地方（你可能需要HTTP代理）。

 3. 运行tmf,自动生成默认配置文件`config.yml`

 4. 用你喜欢的编辑器编辑`config.yml`。

   示例:
 ```yaml
---
# 在使用前将 `enabled` 改为 `true`.
enabled: true
forwarding:
  address: "nats://itsusinn.site:4222"
telegram:
  # TG Bot的token
  token: "114514114:IYokoiYoT4YfU_NA9NzhS5HS5oT-oJTrE"
  # TG Bot的id 即@username
  bot_name: "mesagisto_test_bot"
  webhook:
    enable: false
    heroku: false
    port: 8889
    host: heroku-app-name.herokuapp.com
proxy:
  # 是否启用代理
  enabled: true
  # 现阶段仅允许http代理(reqwest库限制)
  address: "http://127.0.0.1:7890"
# 默认为空, 不推荐手动添加.
# 格式：
# "<chat_id>"= "<channel>" ("str"= "str")
# 例子：
# -11451419 = '10000'
target_address_mapper: {}

 ```
 4. 启动tmf:
 ```shell
 # 给予可执行权限
 $ chmod +x ./tmf
 $ ./tmf
  INFO  telegram_mesaga_fonto > Mesagisto-Bot is starting up
  INFO  telegram_mesaga_fonto > Connecting to nats server
  INFO  telegram_mesaga_fonto > Connected sucessfully,the client id is ***
 # 若要关闭tmf,请使用Ctrl+C
 $ ^C
  INFO  teloxide::dispatching::dispatcher > ^C received, trying to shutdown the dispatcher...
  INFO  teloxide::dispatching::dispatcher > Dispatching has been shut down.
  INFO  telegram_mesaga_fonto::config     > Configuration file was saved
  INFO  telegram_mesaga_fonto             > Mesagisto Bot is going to shut down
 
 ```
 如果没有 [ERROR]输出, 你可以想bot发送 `/help` , 将会得到如下回复:
```text
 信使Bot支持以下命令
 /help - 显示命令帮助
 /enable - 启用消息转发
 /disable - 禁用消息转发
 /setaddress - 设置当前Group的转发地址
```
 5. 创建一个 Telegram 群组, 将bot添加至群组, 并在群组内输入指令:

 `/setaddress <channel>`

> 如果你在使用mirai消息源,channel的值应当是qq号
>
> 实际上无论channel的值如何，只要保证不同转发客户端的值相同即可



## 注意事项

您的Bot应该将Group Privacy Mode设置为 OFF,否则Bot将无法访问群聊消息

