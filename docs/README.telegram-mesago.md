# mirai-mesaga-fonto 
### part of [Mesagisto](https://github.com/MeowCat-Studio/mesagisto)

---


A implementation of message-forwarding-client.
消息转发客户端的 [Telegram](https://core.telegram.org) 实现

___

## Deploy instruction

 1. get binary file at Release page (or check Actions)
 2. place it in where network access to Telegram server is stable (you may need HTTP proxy).
 3. use your favorite editor to create `config.yaml` placed with the binary `telegram-mesaga-fonto`.
 here is an example:
 ```toml
# before use, set `enabled` to `true`.
enabled = 

[forwarding]
address = 'nats://itsusinn.site:4222'

[telegram]
token = 'BOT_TOKEN'
bot_name = ''

[proxy]
enabled = false
# you may want to use 'socks5://your_server:port' to use socks5 proxy,
# however, it was not stable at this stage.
address = 'http://127.0.0.1:8889'

[target_address_mapper_storage]
# default empty, manually add is not recommended.
# <chat_id>= '<channel>' (int= 'int')
#
#-11451419 = '10000'
 ```
 4. start the server:
 ```shell
 $ setsid -f telegram-messaga-fonto 
 ```


