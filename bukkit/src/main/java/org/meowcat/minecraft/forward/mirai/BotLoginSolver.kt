package org.meowcat.minecraft.forward.mirai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.SilentLogger
import net.mamoe.mirai.utils.withSwitch
import org.meowcat.minecraft.forward.Forward
import org.meowcat.minecraft.forward.chunkedHexToBytes
import org.meowcat.minecraft.forward.data.Agent

/**
 * 不仅会处理bot的登陆
 * 还负责将bot通知给调度器
 */
class BotLoginSolver private constructor() {
    companion object {

        private fun login(bot: Bot){
            GlobalScope.launch(Dispatchers.Default){
                try{
                    //这个是保证配置文件非空，所以直接忽略
                    if(bot.id == 123456789L) return@launch
                    //登录
                    bot.login()
                }catch (e:Exception){
                    e.printStackTrace()
                    //登陆失败就从配置中移除
                    Forward.configService.config.botList.forEach {
                        if (it.account == bot.id.toString()){
                            Forward.configService.config.botList.remove(it)
                        }
                    }
                    return@launch
                }
                Forward.botDispatcher.addBot(bot)
            }
        }
        /**
         * 通过指令登陆bot时调用的方法
         */
        fun login(account:Long, passwordMD5: String):Bot{
            //先加入配置
            Forward.configService.config.botList.add(Agent(account,passwordMD5))
            //构建bot对象
            return Bot(account, passwordMD5.chunkedHexToBytes()) {
                //覆盖默认的配置
                //使用"device.json" 保存设备信息
                fileBasedDeviceInfo("Device$account")
                //禁用网络层输出
                networkLoggerSupplier = { SilentLogger }
                //使用bukkit的logger
                botLoggerSupplier = { MiraiLogger("Bot ${it.id}: ").withSwitch() }
                //将登录处理器与bukkit-command结合
                loginSolver = CaptchaSolver()
            }.also { login(it) }
        }

        /**
         * 通过配置登陆Bot的方法
         */
        fun autoLogin(agent:Agent):Bot{
            return Bot(agent.account.toLong(), agent.passwordMD5.chunkedHexToBytes()) {
                //覆盖默认的配置
                //使用"device.json" 保存设备信息
                fileBasedDeviceInfo("Device${agent.account}")
                //禁用网络层输出
                networkLoggerSupplier = { SilentLogger }
                //使用bukkit的logger
                botLoggerSupplier = { MiraiLogger("Bot ${it.id}: ").withSwitch() }
                //将登录处理器与bukkit-command结合
                loginSolver = CaptchaSolver()
            }.also {
                login(it)
            }
        }
    }
}
