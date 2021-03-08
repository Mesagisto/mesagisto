# Mirai 消息源

** Mirai 用于 [Mesagisto 信使](https://github.com/MeowCat-Studio/mesagisto) 的功能实现，功能为转发消息到客户端(Tencent-QQ) **

## 需求

- Mirai 2.12.0+, MCL 2.1.0+

- 前置插件[ChatCommand](https://github.com/project-mirai/chat-command)

!!! Warning
    信使会自动配置指令权限, 请不要使用 Permission 命令操作信使的权限(因每次启动时会重置)
## 安装

=== "手动安装"

	在 [Releases页面](https://github.com/MeowCat-Studio/mirai-message-source/releases) 下载jar归档文件。 移至 mirai-console(mcl) 同目录下的plugins文件夹下

=== "MCL自动安装 稳定版"

	使用mcl命令 `./mcl -a org.mesagisto:mirai-message-source -n maven --type plugin`

	每次启动时使用`./mcl -u`更新即可
 
	使用mcl命令 `./mcl -r org.mesagisto:mirai-message-source` 卸载

=== "MCL自动安装 最新"

	使用mcl命令 `./mcl -a org.mesagisto:mirai-message-source -n latest --type plugin`

	每次更新时需要删掉plugins内 mirai-message-source-latest.mirai2.jar 才能更新到最新版

	注意: 由于最新版仅发布在GitHub Release上, 所以使用了 [GH-Proxy](https://ghproxy.com/)

	但在某些地区访问仍旧受阻,如无法访问可修改 mcl 配置文件`config.json` 中 proxy代理选项
 
	使用mcl命令 `./mcl -r org.mesagisto:mirai-message-source` 卸载


## 简单入门

1. 运行一次 MCL 并关闭

2. 在 MCL 目录下 找到配置文件 config/org.mesagisto.mirai-message-source/config.yml 并修改
```yaml
# 加密设置
cipher:
  # 加密用使用的密钥 需保证各端相同
  key: your-key
# 网络代理, 下载Telegram或Discord图片时所需
# 注意, 如果tg-bot/dc-bot与mirai-bot在同一台主机
# 仅设置tg-bot/dc-bot的代理即可
proxy:
  # 是否启用代理、
  # 如无Telegram或Discord，false即可
  enable: false
  # 代理服务器地址
  address: 'http://127.0.0.1:7890'
# 实验性权限配置
perm: 
  # 严格模式, 当启用时
  # 信使仅对下方users列表内用户所发命令作出响应
  # 当禁用时, 信使对所有用户所发指令作出响应
  # 但频道绑定仅允许管理员操作
  strict: false
  # 用户列表, QQ号
  users: 
    - 123456
# 存放信使频道与QQ群的对应关系,默认为空. 不推荐手动添加.
bindings: {}
```

3. 在 **QQ 群聊** (而不是 Mirai 控制台)中可以执行以下指令 `/msgist` 或 `/信使` 将会得到
```text
    参数不匹配, 你是否想执行: 
    /msgist about    (参数不足)
    /msgist ban <user>    (参数不足)
    /msgist bind <channel>    (参数不足)
    /msgist disable <group/channel>    (参数不足)
    /msgist enable <group/channel>    (参数不足)
    /msgist status    (参数不足)
    /msgist unban <user>    (参数不足)
    /msgist unbind    (参数不足)
```
4. 使用 /msgist bind 频道名 即可绑定到信使频道

## 注意事项
 1. 需要转发的消息源需要绑定同一个 ** 频道名**
 2. 黑名单功能需要开启严格模式
