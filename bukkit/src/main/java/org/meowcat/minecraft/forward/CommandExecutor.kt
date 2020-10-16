package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import net.mamoe.mirai.Bot

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.meowcat.minecraft.forward.Forward.Companion.botDispatcher
import org.meowcat.minecraft.forward.mirai.BotLoginSolver
import org.meowcat.minecraft.forward.mirai.captchaChannel

class CommandExecutor :SuspendingCommandExecutor{

    override suspend fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val senderName = if (sender is ConsoleCommandSender) "^Console^" else sender.name
        //垃圾bukkit
        if (!sender.hasPermission("forward.use")){
            sender.sendMessage("权限不足")
            return false
        }
        //手动命令解析
        when(args[0]){
            "login" -> {
                if (args.size != 3) return false
                val account = args[1].toLong()
                val password = args[2]

                //检查bot是否已经记录
                for (bot in allBots) {
                    if (bot.id == account) {
                        logger.warning("$account 已登录,切勿重复登陆")
                        return false
                    }
                }
                val bot:Bot
                try {
                    //构造bot
                     bot = BotLoginSolver.login(account, password.md5.chunkedHexToBytes())
                }catch (e:Exception){
                    e.printStackTrace()
                    return false
                }
                //把bot保存
                Forward.botDispatcher.addBot(bot).reDispatch()
                //将bot的操作者记录下来
                Forward.operating[account] = senderName
                return true
            }
            "captcha" -> {
                //验证码接收
                when (args.size) {
                    //当是字符验证码时，参量为3
                    3 -> {
                        val bot = botDispatcher.findBotByID(args[1].toLong()) ?: return false
                        bot.captchaChannel.send(args[2])
                    }
                    //其他验证码时，参量为2
                    2 -> {
                        val bot = botDispatcher.findBotByID(args[1].toLong()) ?: return false
                        bot.captchaChannel.send("R")
                    }
                }
                return true
            }
            "help" -> {

            }
            "setTarget" -> {
                if (args[1].isEmpty()) return false
                botDispatcher.changeTarget(args[1].toLong())
            }
            else -> {
                sender.sendMessage("输入/forward help获得帮助")
                return true
            }
        }
        //你不对劲
        return false
    }

}