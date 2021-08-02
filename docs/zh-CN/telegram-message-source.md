# telegram-mesaga-fonto 

**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的[Telegram](https://core.telegram.org) 实现。**

## 部署指导

 1. 在 [Release页面](https://github.com/MeowCat-Studio/telegram-mesaga-fonto/releases)获取二进制文件(简称tmf)。

 2. 将tmf放在网络访问Telegram服务器稳定的地方（你可能需要HTTP代理）。

 3. 运行tmf,自动生成默认配置文件`config.toml`

 4. 用你喜欢的编辑器编辑config.toml`。

   示例:
 ```toml
# 在使用前将 `enabled` 改为 `true`.
enabled = true

[forwarding]
address = 'nats://itsusinn.site:4222'

[telegram]
# TG Bot的token
token = 'BOT_TOKEN'
# TG Bot的id
bot_name = 'BOT_NAME'

[proxy]
# 是否启用代理
enabled = false
# 现阶段仅允许http代理(reqwest库限制)
address = 'http://127.0.0.1:1080'

[target_address_mapper_storage]
# 默认为空, 不推荐手动添加.
# 格式：
# <chat_id>= '<channel>' (int= 'int')
# 例子：
# -11451419 = '10000'
 ```
 4. 启动tmf:
 ```shell
 $ ./tmf
  [INFO] telegram-message-fonto started.	
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

您的Bot应该将Group Privacy设置为 ON,否则Bot将无法访问群聊消息
