# Telegram 消息源

** [Mesagisto 信使](https://github.com/MeowCat-Studio/mesagisto) 的功能实现，功能为转发消息到 Telegram 客户端 **

## 需求

1. Bot 应该在 BotFather 处将 Group Privacy Mode 设置为 OFF,否则你的 Bot 将无法访问群聊消息.

## 部署

1. 在 [Release页面](https://github.com/MeowCat-Studio/telegram-message-source/releases) 获取二进制文件(以下简称tms)
!!! Note
     文件命名规则：tg-<架构>-<操作系统>-<特性>
     
     二进制对于 Windows 用户而言, 可执行文件会带有 colored 后缀，colored 版本的文件有终端的颜色代码，PS(PowerShell)下可能出现乱码。
     推荐有 MINGW 终端的 Winodws 用户下载该版本

2. 确保 tms 能在稳定访问访问 Telegram 服务器的网络环境下（可能需要HTTP代理,详见本文档配置文件部分）

3. 运行 tms ,自动生成默认配置文件 `config/tg.yml`

4. 编辑配置文件 `config/tg.yml`
```yaml
---
# 在使用前将 `enable` 改为 `true`.
enable: true
# 中间转发服务器,消息的桥梁. 
# 默认为信使公益[NATS](https://github.com/nats-io/nats-server)服务器
nats:
  address: "nats://nats.mesagisto.org:4222"
# 加密设置
cipher:
  # 加密用使用的密钥
  key: test
telegram:
  # TG Bot的token密钥,于@BotFather处获取
  token: "114514191:IYokoiYoT4YfU_NA9NzhS5HS5oT-oJTrE"
proxy:
  # 是否启用代理
  enable: true
  # 现阶段仅允许http代理(reqwest库限制)
  address: "http://127.0.0.1:7890"
# 存放信使频道与TG群组的对应关系,默认为空. 不推荐手动添加.
bindings: {}
```

5. 启动 tms:
```shell
# 给予可执行权限
$ chmod +x ./tms
# 运行
$ ./tms
# 若要关闭tms,请使用Ctrl+C,切忌不平滑关闭
$ ^C
```
如果没有 [ERROR]输出, 你可以向bot发送 `/help` , 将会得到如下回复:
```
信使Bot支持以下命令

/about — 关于本项目
/unbind — 解绑当前群组的转发地址
/help — 显示命令帮助
/status — 显示状态
/bind — 绑定当前群组的转发地址
```

6. 创建一个 Telegram 群组, 将 Bot 添加至群组, 并在群组内输入指令:
`/bind <channel>`

## 注意事项

1. 无论 channel 的值如何，只要保证各个转发客户端绑定的频道相同即可
2. 中途变更 Group Privacy Mode 后, 请将 Bot 移除出群组并重启.
