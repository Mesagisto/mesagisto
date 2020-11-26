package org.meowcat.minecraft.forward.mirai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.LoginSolver
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.kodein.di.DI
import org.kodein.di.instance
import org.meowcat.minecraft.forward.BotDispatcher
import java.io.File


val Bot.captchaChannel by lazy { Channel<String>() }

class CaptchaSolver(di:DI) : LoginSolver() {
   private val bd:BotDispatcher by di.instance()
   private val creators = bd.creators

   private val consoleName = "CONSOLE"

   override suspend fun onSolvePicCaptcha(bot: Bot, data: ByteArray): String {
      val logger = bot.logger
      val tempFile: File = createTempFile(suffix = ".png").apply { deleteOnExit() }
      var senderName:String = creators[bot.id] ?: error("Not creator found")
      logger.info("需要图片验证码登录, 验证码为 4 字母")
      withContext(Dispatchers.IO) {
         try {
            tempFile.createNewFile()
            tempFile.writeBytes(data)
            logger.info("请查看文件 ${tempFile.absolutePath}")
         } catch (e: Exception) {
            e.printStackTrace()
         }
      }
      when (senderName){
         consoleName ->{
            logger.info("请输入 /forward captcha [4位字母验证码]. 若要更换验证码,请直接/forward captcha")
         }
         else -> {
            senderName = senderName.substring(2)
            Bukkit.getPlayer(senderName)?.sendMessage("需要 4 位字母验证码，请登陆服务器以查看图片")
         }
      }

      //需要验证码，开启通道并  通知登录命令的发送者
      return bot.captchaChannel.receive()
   }

   override suspend fun onSolveSliderCaptcha(bot: Bot, url: String): String {
      var senderName:String = creators[bot.id] ?: error("Not creator found")
      val reply = """
            需要滑动验证码
            请在任意浏览器中打开以下链接并完成验证码.
            完成后请输入/forward captcha QQ号码
        """.trimIndent()
      when(senderName){
         consoleName -> {
            bot.logger.info(reply)
         }
         else -> {
            senderName = senderName.substring(2)
            Bukkit.getPlayer(senderName)?.sendMessage(reply)
            val message = TextComponent("验证链接")
            message.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
            message.color = ChatColor.YELLOW
            Bukkit.getPlayer(senderName)?.spigot()?.sendMessage(message)
         }
      }

      //需要验证码，开启通道并通知登录命令的发送者
      return bot.captchaChannel.receive()
   }

   override suspend fun onSolveUnsafeDeviceLoginVerify(bot: Bot, url: String): String {
      var senderName:String = creators[bot.id] ?: error("Not creator found")
      val reply = """
            ${bot.id}
            需要进行账户安全认证
            该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题
            完成以下账号认证即可成功登录|理论本认证在mirai每个账户中最多出现1次
            请将该链接在浏览器中打开并完成认证
            成功后输入/forward captcha QQ号码
        """.trimIndent()
      when(senderName){
         consoleName -> {
            bot.logger.info(reply)
            bot.logger.info(url)
         }
         else -> {
            senderName = senderName.substring(2)
            val player = Bukkit.getPlayer(senderName)
               ?: error("Player not found")
            player.sendMessage(reply)
            val message = TextComponent("验证链接")
            message.color = ChatColor.YELLOW
            message.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
            player.spigot().sendMessage(message)
         }
      }

      //需要帐号认证 通知登录命令的发送者
      return bot.captchaChannel.receive()
   }
}