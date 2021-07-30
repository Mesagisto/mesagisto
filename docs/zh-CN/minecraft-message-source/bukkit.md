# bukkit消息源
**[Mesagisto信使项目](https://github.com/MeowCat-Studio/mesagisto)的一部分，消息转发客户端的bukkit(Minecraft)实现。**

## Depoly

1. 在[Releases页面](https://github.com/MeowCat-Studio/bukkit-mesaga-fonto/releases) 下载mesagisto.jar。

2. 将jar包移动至bukkit系服务端(如Spigot,Paper等)的plugins文件夹下。

3. 启动服务器,此时会自动生成配置文件。

4. 修改plugins/mesagisto/config.yml，

   enable改为true

   channel改为 `'你的qq号'` 

   例如`channel: '123456789'`。此处的qq号为您个人(指用户)的qq号

   其他字段保持默认值即可

   最终,配置文件将会是这样的
   ```
   enable: true
   nats: 'nats://itsusinn.site:4222'
   channel: '180265xxxx'
   lang: 'zh_CN'
   ```

5. 保存配置文件，重启bukkit服务端。