package org.meowcat.minecraft.forward

import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.SilentLogger
import net.mamoe.mirai.utils.withSwitch

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

import kotlin.coroutines.CoroutineContext

class CommandExecutor :CommandExecutor,CoroutineScope{

    private val bots: HashSet<Bot>
        get() = Forward.bots

    private val operating:HashMap<Long,String>
        get() = Forward.operating

    private val captchaChannel:HashMap<Long,Channel<String>>
        get() = Forward.captchaChannel

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    override fun onCommand(sender_: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val senderName = sender_.name
        launch {
            if (args[0]=="login"&&args.size==3) {
                val account = args[1].toLong()
                val password = args[2]
                val bot = Bot(account, password) {
                    // 覆盖默认的配置
                    fileBasedDeviceInfo("device.json") // 使用 "device.json" 保存设备信息
                    networkLoggerSupplier = {
                        SilentLogger
                    } // 禁用网络层输出
                    botLoggerSupplier = {
                        MineLogger("Bot ${it.id}: ")
                                .withSwitch()
                    }
                    loginSolver = MineLoginSolver()
                }.alsoLogin().apply {
                    subscribeAlways<GroupMessageEvent> {
                        if (group.id == 226556947L) {
                            Bukkit.broadcastMessage("<${sender.nameCardOrNick}> ${message.content}")
                        }
                    }
                }
                operating[bot.id]=senderName
                bots.add(bot)
            }else if (args[0]=="captcha"){
                //验证码接收
                launch {
                    when(args.size){
                        3 -> {
                            captchaChannel[args[1].toLong()]!!.send(args[1])
                        }
                        2 -> {
                            captchaChannel[args[1].toLong()]!!.send("\n")
                        }
                    }
                }
            }
        }
        //执行完毕
        return true
    }

}