package org.meowcat.minecraft.forward.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.network.LoginFailedException
import net.mamoe.mirai.utils.LoginSolver
import net.mamoe.mirai.utils.SilentLogger
import net.mamoe.mirai.utils.withSwitch
import org.kodein.di.DI
import org.kodein.di.instance
import org.meowcat.minecraft.forward.BotDispatcher
import org.meowcat.minecraft.forward.chunkedHexToBytes
import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.mirai.CaptchaSolver
import org.meowcat.minecraft.forward.mirai.MiraiLogger
import kotlin.coroutines.CoroutineContext

/**
 * 不仅会处理bot的登陆
 * 还负责将bot通知给调度器
 */
class BotLoginService(private val di:DI): CoroutineScope{
   override val coroutineContext: CoroutineContext by di.instance("async")

   private val configService: ConfigService by di.instance()
   private val loginSolver: CaptchaSolver by di.instance()
   private val bd: BotDispatcher by di.instance()
   //用于保存机器人创建者的map

   /**
    * 通过指令登陆bot时调用的方法
    * 登陆过程不阻塞
    * @throws LoginFailedException
    */
   fun login(account:Long, passwordMD5: String):Bot{
      //先加入配置
      configService.config.botList.add(Agent(account,passwordMD5))

      val bot = Bot(account, passwordMD5.chunkedHexToBytes()) {
         //覆盖默认的配置
         //使用"device.json" 保存设备信息
         fileBasedDeviceInfo("Device$account")
         //禁用网络层输出
         networkLoggerSupplier = { SilentLogger }
         //使用bukkit的logger
         botLoggerSupplier = { MiraiLogger(di,"Bot ${it.id}: ").withSwitch() }
         //将登录处理器与bukkit-command结合
         loginSolver = this@BotLoginService.loginSolver
      }
      //构建bot对象
      loginAsync(bot)
      return bot
   }
   /**
    * 通过配置登陆Bot的方法
    * 登陆过程不阻塞
    * @throws LoginFailedException
    */
   fun autoLogin(agent:Agent):Bot{
      return Bot(agent.account.toLong(), agent.passwordMD5.chunkedHexToBytes()) {
         //覆盖默认的配置
         //使用"device.json" 保存设备信息
         fileBasedDeviceInfo("Device${agent.account}")
         //禁用网络层输出
         networkLoggerSupplier = { SilentLogger }
         //使用bukkit的logger
         botLoggerSupplier = { MiraiLogger(di,"Bot ${it.id}: ").withSwitch() }
         //将登录处理器与bukkit-command结合
         loginSolver = this@BotLoginService.loginSolver
      }.also {
         loginAsync(it)
      }
   }

   /**
    * @throws LoginFailedException
    */
   private fun loginAsync(bot: Bot) {
      try{
         //这个默认账号是为了保证配置文件非空，所以直接忽略
         if(bot.id == 123456789L) return
         //登录
         launch(Dispatchers.Default){
            bot.login()
         }
      }catch (e: LoginFailedException){
         //登陆失败就从配置中移除
         configService.config.botList.forEach {
            if (it.account == bot.id.toString()){
               configService.config.botList.remove(it)
            }
         }
         throw e
      }
      bd.addBot(bot)
   }

}
