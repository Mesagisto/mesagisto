# bukkit message source
**Part of the [Mesagisto  Project](https://github.com/MeowCat-Studio/mesagisto), the bukkit (Minecraft) implementation of the message forwarding client. **

## Depoly

1. Download bmf.jar from [Releases page](https://github.com/MeowCat-Studio/bukkit-mesaga-fonto/releases).

2. Move the jar package to the plugins folder of the bukkit server (such as Spigot, Paper, etc.). 

3. Start the server, the configuration file will be generated automatically.

4. modify plugins/mesagisto/config.yml.

   enable to true

   change channel to **your qq number**

   For example `channel: '123456789'`. The qq number here is your personal (meaning user) qq number

   > In fact, whatever the value of the channel is, just make sure the value is the same for different forwarding clients

   Just leave the other fields as default

   In the end, the configuration file will look like this
   ```
   enable: true
   nats: 'nats://itsusinn.site:4222'
   channel: '180265xxxx'
   lang: 'zh_CN'
   ```

5. Save the configuration file and restart the bukkit server.

