------

## 前言

What is this？

本插件EasyForward是一款给予了MC服务器向QQ群发送消息能力的插件

为纯Kotlin实现，相比于之前的相似项目，本项目依赖于 Mirai

------

## 效果图

![A](https://s1.ax1x.com/2020/10/18/0OyS8x.png)

![B](https://s1.ax1x.com/2020/10/18/0O6c7Q.png)

------
## 特性

- 负载均衡：可同时登陆多个Bot，防止由于吞吐量过大导致Bot被风控
- 配置简单：仅通过两个简单指令，无需多余配置就可使用本插件
- 自动登陆：指令登录Bot，设置目标QQ群号后就可写入配置，下次启动服务器时可自动从配置文件中读取并登录


------
## 使用方法

```Bukkit
/forward add QQ帐号 QQ密码 来登录一个bot
/forward setTarget QQ群号 来设置需要转发的qq群
```

有时需要进行验证码验证，听从插件提示即可

 权限  `forward.use` ：默认仅OP可使用

------

## 下载地址

##### 国内

链接：https://pan.baidu.com/s/1mHm_P2o3uGwMK5MXfn0rZg
提取码：e04a 

##### 国外

https://github.com/Itsusinn/Minecraft-Forward/releases

------

## TODO-计划之中的功能

- 与多世界插件相结合，提供多事件与多群聊相沟通的能力
- 将玩家登录，离线，获得成就等事件消息加入转发内容

------

## 后记

本插件使用了Kotlin进行开发，开发的过程中发现Bukkit几乎不允许主线程以外的线程访问

这对协程框架是一个打击性的灾难，而https://github.com/Shynixn/MCCoroutine则提供了一个Minecraft调度器

调度器控制着协程应该在哪个线程上运行

MCCoroutine的Minecraft调度器 使得用withContext得以便利地从IO或者Default线程池快速切换到mc主线程

既没有阻塞主线程，也得以解决bukkit的异步限制问题

------

## 开源协议&引用,参考的项目

**`AGPLv3`** 

- https://github.com/mamoe/mirai
- https://github.com/ryoii/mirai-console-addition
- https://github.com/Kotlin/kotlinx.coroutines
- https://github.com/Kotlin/kotlinx.serialization
- https://github.com/charleskorn/kaml
- https://github.com/Shynixn/MCCoroutine

------

