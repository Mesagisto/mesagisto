package org.meowcat.minecraft.forward.mirai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.SilentLogger
import net.mamoe.mirai.utils.withSwitch
import org.meowcat.minecraft.forward.decrypt

class BotLoginSolver(val key:String) {
    companion object {
        //传输验证码的通道
        val captchaChannel by lazy { HashMap<Long, Channel<String>>() }

        private fun login(bot: Bot){
            GlobalScope.launch (Dispatchers.Default){
                bot.login()
            }
        }

        fun login(account:Long,password:String):Bot{
            //构建bot对象
            return Bot(account, password) {
                // 覆盖默认的配置
                // 使用 "device.json" 保存设备信息
                fileBasedDeviceInfo("device${account.toString().decrypt(account.toString())}.json")
                //禁用网络层输出
                networkLoggerSupplier = { SilentLogger }
                //使用bukkit的logger
                botLoggerSupplier = { MiraiLogger("Bot ${it.id}: ").withSwitch() }
                //将登录处理器与bukkit-command结合
                loginSolver = MiraiLoginSolver()
            }.also {
                login(it)
            }

        }

        fun logout(bot: Bot){
            GlobalScope.launch (Dispatchers.Default) {
                bot.close()
            }
        }
    }
}