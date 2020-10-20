package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import net.mamoe.mirai.Bot
import net.md_5.bungee.api.ChatColor

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.meowcat.minecraft.forward.BotDispatcher.allBots
import org.meowcat.minecraft.forward.mirai.BotLoginSolver

object ForwardCommandExecutor :SuspendingCommandExecutor{

    override suspend fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val senderName = if (sender is ConsoleCommandSender) "^Console^" else sender.name
        //手动命令解析
        when(args[0]){
            "login" -> {
                if (args.size != 3) return false
                val account = args[1].toLong()
                val password = args[2]

                //检查bot是否已经记录
                for (bot in allBots) {
                    if (bot.id == account) {
                        sender.sendMessage("$account 已登录,切勿重复登陆".toTextComponent(ChatColor.YELLOW))
                        return false
                    }
                }
                val bot:Bot
                try {
                    //构造bot
                     bot = BotLoginSolver.login(account, password.md5)
                }catch (e:Exception){
                    e.printStackTrace()
                    return false
                }
                //把bot保存
                BotDispatcher.addBot(bot).reDispatch()
                //将bot的操作者记录下来
                Forward.operating[account] = senderName
                return true
            }
            "captcha" -> {
                //验证码接收
                when (args.size) {
                    //当是字符验证码时，参量为3
                    3 -> {
                        val bot = BotDispatcher.findBotByID(args[1].toLong()) ?: return false
                        bot.captchaChannel.send(args[2])
                    }
                    //其他验证码时，参量为2
                    2 -> {
                        val bot = BotDispatcher.findBotByID(args[1].toLong()) ?: return false
                        bot.captchaChannel.send("R")
                    }
                }
                return true
            }
            "help" -> {
                val reply = """
                    /forward add QQ帐号 QQ密码 来登录一个bot
                    /forward setTarget QQ群号 来设置需要转发的qq群
                """.trimIndent()
                sender.sendMessage(reply.toTextComponent(ChatColor.YELLOW))
            }
            "setTarget" -> {
                if (args[1].isEmpty()) return false
                BotDispatcher.changeTarget(args[1].toLong())
            }
            else -> {
                sender.sendMessage("输入/forward help获得帮助".toTextComponent(ChatColor.YELLOW))
                return true
            }
        }
        //你不对劲
        return false
    }

}