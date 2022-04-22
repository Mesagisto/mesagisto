# Telegram消息源

**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的Telegram 实现。**

## 部署

 1. 在 [Release页面](https://github.com/MeowCat-Studio/telegram-message-source/releases)获取二进制文件(简称tms)。

 2. 确保tms在能稳定访问访问Telegram服务器的网络环境下（可能需要HTTP代理,详见本文档配置文件部分）。

 3. 运行tms,自动生成默认配置文件`config/tg.yml`

 4. 编辑配置文件`config/tg.yml`。

   示例:
  ```yaml
  ---
  # 在使用前将 `enable` 改为 `true`.
  enable: true
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
  telegram:
    # TG Bot的token,于@BotFather处获取
    token: "114514114:IYokoiYoT4YfU_NA9NzhS5HS5oT-oJTrE"
    # TG Bot的id 即@username的username部分
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
  # 存放信使频道与TG群组的对应关系,默认为空. 不推荐手动添加.
  target_address_mapper: {}
  ```
 4. 启动tms:
 ```shell
 # 给予可执行权限
 $ chmod +x ./tms
 $ ./tms
  INFO  telegram_message_source > Mesagisto-Bot is starting up
  INFO  telegram_message_source > Connecting to nats server
  INFO  telegram_message_source > Connected sucessfully,the client id is ***
 # 若要关闭tmf,请使用Ctrl+C,切忌不平滑关闭
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

> 此处channel的值为应设置的信使频道
>
> 无论channel的值如何，只要保证不同转发客户端的值相同即可



## 注意事项

- 您的Bot应该在BotFather处将Group Privacy Mode设置为 OFF,否则Bot将无法访问群聊消息.
- 变更Group Privacy Mode或是群组类型后,请将Bot移除出群组并重启.


