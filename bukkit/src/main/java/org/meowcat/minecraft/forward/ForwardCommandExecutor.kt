package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import net.mamoe.mirai.Bot
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.kodein.di.DI
import org.kodein.di.instance
import org.meowcat.minecraft.forward.extension.failure
import org.meowcat.minecraft.forward.extension.sendMessage
import org.meowcat.minecraft.forward.extension.success
import org.meowcat.minecraft.forward.extension.toTextComponent
import org.meowcat.minecraft.forward.mirai.captchaChannel
import org.meowcat.minecraft.forward.service.BotLoginService
import java.util.logging.Logger

val HelpReply = arrayOf(
   "/forward add QQ帐号 QQ密码 来登录一个bot \n".toTextComponent(ChatColor.YELLOW),
   "/forward setTarget QQ群号 来设置需要转发的qq群".toTextComponent(ChatColor.YELLOW)
)

class ForwardCommandExecutor(di:DI) :SuspendingCommandExecutor{

   private val bd:BotDispatcher by di.instance()
   private val botLoginService: BotLoginService by di.instance()

   private val logger:Logger by di.instance()

   override suspend fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
      val senderName = sender.name

      if (args.isEmpty()){
         sender.sendMessage(*HelpReply)
         return false
      }

      //手动命令解析
      when(args[0]){
         "add" -> {
            if (args.size != 3) return false
            val account = args[1].toLong()
            val password = args[2]

            //检查bot是否已经记录
            bd.allBots.forEach{
               if (it.id == account) {
                  sender.sendMessage("$account 已登录,切勿重复登陆".toTextComponent(ChatColor.YELLOW))
                  return failure
               }
            }
            val bot:Bot
            //将bot的操作者记录下来
            bd.operating[account] = senderName
            logger.info(senderName)
            try {
               bot = botLoginService.login(account, password.md5)
            }catch (e:Exception){ throw e }
            //把bot保存
            bd.addBot(bot).reDispatch()
            return success
         }
         "captcha" -> {
            //验证码接收
            when (args.size) {
               //当是字符验证码时，参量为3
               3 -> {
                  val bot = bd.findBotByID(args[1].toLong()) ?: return false
                  bot.captchaChannel.send(args[2])
               }
               //其他验证码时，参量为2
               2 -> {
                  val bot = bd.findBotByID(args[1].toLong()) ?: return false
                  bot.captchaChannel.send("R")
               }
            }
            return true
         }
         "help" -> sender.sendMessage(*HelpReply)

         "settarget" -> {
            if (args.size != 2) return false
            bd.changeTarget(args[1].toLong())
         }

         "status" -> {
            bd.allBots.forEach {
               logger.info("${it.nick} ${it.id}")
            }
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