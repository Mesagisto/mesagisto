package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.SilentLogger
import net.mamoe.mirai.utils.withSwitch

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

class CommandExecutor :SuspendingCommandExecutor{

    private val bots: HashMap<Long, Bot>
        get() = Forward.listeners

    private val operating:HashMap<Long,String>
        get() = Forward.operating


    private val captchaChannel:HashMap<Long,Channel<String>>
        get() = Forward.captchaChannel

    override suspend fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val senderName = if (sender is ConsoleCommandSender) "^Console^" else sender.name

        if (!sender.hasPermission("forward.use")){
            sender.sendMessage("权限不足")
            return false
        }
        when(args[0]){
            "unlock" -> {
                if (args.size!=2) return false
                return true
            }
            "login" -> {
                if (args.size!=3) return false
                val account = args[1].toLong()
                val password = args[2]
                for(bot_ in bots.values){
                    if (bot_.id==account) return false
                    logger.warning("$account 已登录,切勿重复登陆")
                }
                //构建bot对象
                val bot = Bot(account, password) {
                    // 覆盖默认的配置
                    // 使用 "device.json" 保存设备信息
                    fileBasedDeviceInfo("device.json")
                    //禁用网络层输出
                    networkLoggerSupplier = { SilentLogger }
                    //使用bukkit的logger
                    botLoggerSupplier = { MineLogger("Bot ${it.id}: ").withSwitch() }
                    //将登录处理器与bukkit-command结合
                    loginSolver = MineLoginSolver()
                }.apply {
                    val bot = this
                    //监听group的消息并广播到mc服务器
                    launch (Dispatchers.Default){
                        bot.login()
                    }
                }
                //将bot的操作者记录下来
                operating[bot.id]=senderName
                //把bot保存
                bots[bot.id] = bot
                return true
            }
            "captcha" -> {
                //验证码接收
                when(args.size){
                    3 -> captchaChannel[args[1].toLong()]!!.send(args[1])
                    2 -> captchaChannel[args[1].toLong()]!!.send(" ")
                }
                return true
            }
        }

        //你不对劲
        return false
    }

}
