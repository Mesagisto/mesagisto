package io.github.itsusinn.easyforward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import io.github.itsusinn.easyforward.bukkit.extension.md5
import io.github.itsusinn.easyforward.bukkit.extension.sendMessage
import io.github.itsusinn.easyforward.bukkit.extension.toTextComponent
import io.github.itsusinn.easyforward.bukkit.extension.captchaChannel
import io.github.itsusinn.easyforward.bukkit.service.BotDispatcher
import io.github.itsusinn.easyforward.bukkit.service.BotLoginService
import io.github.itsusinn.easyforward.bukkit.service.ConfigService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

val HelpReply = arrayOf(
   "/forward add QQ帐号 QQ密码 来登录一个bot".toTextComponent(ChatColor.YELLOW),
   "/forward setTarget QQ群号 来设置需要转发的qq群".toTextComponent(ChatColor.YELLOW),
   "/forward smms token to add a smms token".toTextComponent(ChatColor.YELLOW)
)

class ForwardCommandExecutor(di:DI,) :SuspendingCommandExecutor,CoroutineScope{

   private val bd: BotDispatcher by di.instance()
   private val botLoginService: BotLoginService by di.instance()
   private val configService: ConfigService by di.instance()
   private val config = configService.config
   private val logger:Logger by di.instance()
   override val coroutineContext: CoroutineContext = Dispatchers.Default

   override suspend fun onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array<out String>
   ): Boolean {
      val senderName =
         if (sender is Player) "P-${sender.name}"
         else sender.name

      if (args.isEmpty()){
         sender.sendMessage(*HelpReply)
         return false
      }

      //手动命令解析
      when(args[0].toLowerCase()){
         "add" -> {
            if (args.size != 3) return false
            val account = args[1].toLong()
            val password = args[2]

            //检查bot是否已经记录
            bd.allBots.forEach {
               if (it.id == account && it.isOnline) {
                  sender.sendMessage("$account 已登录,切勿重复登陆".toTextComponent(ChatColor.YELLOW))
                  return false
               }
            }
            //将bot的操作者记录下来
            bd.creators[account] = senderName

            try {
               botLoginService.commandLogin(account, password.md5)
            } catch (e: Exception) {
               throw e
            }

            return true
         }
         "captcha" -> {
            //验证码接收
            when (args.size) {
               //当是字符验证码时，参量为3
               3 -> {
                  val bot = bd.findBotByID(args[1].toLong())
                     ?: error("Bot Not Found")
                  bot.captchaChannel.send(args[2])
               }
               //其他验证码时，参量为2
               2 -> {
                  val bot = bd.findBotByID(args[1].toLong())
                     ?: error("Bot Not Found")
                  bot.captchaChannel.send("Other")
               }
               else -> {
                  sender.sendMessage(*HelpReply)
               }
            }
            return true
         }
         "help" -> sender.sendMessage(*HelpReply)
         "setTarget".toLowerCase() -> {
            if (args.size != 2) return false
            bd.changeTarget(args[1].toLong())
         }
         "status" -> {
            bd.allBots.forEach {
               logger.info("${it.nick} ${it.id} ${it.isOnline}")
            }
         }
         "smms" -> {
            if (args.size!=2)return false
            config.smms = args[1]
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