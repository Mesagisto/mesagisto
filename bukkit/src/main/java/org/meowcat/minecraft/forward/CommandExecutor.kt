package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.mirai.BotLoginSolver
import org.meowcat.minecraft.forward.mirai.BotLoginSolver.Companion.captchaChannel

class CommandExecutor :SuspendingCommandExecutor{

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
                //把bot保存
                Forward.allBots[Agent(args[1],password)] = BotLoginSolver.login(account, password)
                //检查bot是否已经记录
                for(bot in allBots.values){
                    if (bot.id==account) return false
                    logger.warning("$account 已登录,切勿重复登陆")
                    return false
                }
                //将bot的操作者记录下来
                Forward.operating[Agent(account,password)] = senderName
                return true
            }
            "captcha" -> {
                //验证码接收
                when(args.size){
                    //当是字符验证码时，参量为3
                    3 -> captchaChannel[args[1].toLong()]!!.send(args[1])
                    //其他验证码时，参量为2
                    2 -> captchaChannel[args[1].toLong()]!!.send(" ")
                }
                return true
            }
        }

        //你不对劲
        return false
    }

}
