package i.g.i.easyforward.bukkit.extension

import i.g.i.easyforward.bukkit.service.BotDispatcher
import i.g.i.easyforward.bukkit.service.ImageUploadService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.LoginSolver
import net.mamoe.mirai.utils.MiraiLoggerPlatformBase
import org.bukkit.Bukkit
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

val Bot.captchaChannel by lazy { Channel<String>() }

class CaptchaSolver(di: DI) : LoginSolver() {
   private val bd: BotDispatcher by di.instance()
   private val creators = bd.creators

   private val imageUploadService: ImageUploadService by di.instance()
   private val console = Bukkit.getConsoleSender()

   override suspend fun onSolvePicCaptcha(bot: Bot, data: ByteArray): String {
      val tempFile: File = File("${bot.id}.jpg").apply { deleteOnExit() }
      val imgUrl:String
      val senderName:String = creators[bot.id] ?: error("Not creator found")
      val sender = getCommandSender(senderName)

      sender.sendMessage("""
         ${bot.id} 需要图片验证码登录, 验证码为 4 字母
      """.trimIndent())

      withContext(Dispatchers.IO) {
         tempFile.createNewFile()
         tempFile.writeBytes(data)
         imgUrl = imageUploadService.upload(tempFile)
            ?: error("上传图床失败")
      }

      val urlMessage = makeHoverClickUrl("验证码链接", imgUrl)
      console.sendMessage(imgUrl)
      sender.sendMessage(urlMessage)
      sender.sendMessage("""
         请输入 /forward captcha ${bot.id} [4位字母验证码]. 
         若要更换验证码,请直接/forward captcha ${bot.id}
      """.trimIndent())
      //需要验证码，开启通道并 通知登录命令的发送者
      val captcha = bot.captchaChannel.receive()
      sender.sendMessage("接收到验证码 $captcha")
      return captcha
   }

   override suspend fun onSolveSliderCaptcha(bot: Bot, url: String): String {
      val senderName:String = creators[bot.id] ?: error("Not creator found")
      val reply = """
            ${bot.id} 需要滑动验证码
            请在任意浏览器中打开以下链接并完成验证码.
            完成后请输入/forward captcha ${bot.id}
        """.trimIndent()

      val sender = getCommandSender(senderName)
      val message = makeHoverClickUrl("滑动验证码验证链接", url)

      bot.logger.info(url)

      sender.sendMessage(reply)
      sender.sendMessage(message)

      //需要验证码，开启通道并通知登录命令的发送者
      val captcha = bot.captchaChannel.receive()
      sender.sendMessage("接收到验证码 $captcha")
      return captcha
   }

   override suspend fun onSolveUnsafeDeviceLoginVerify(bot: Bot, url: String): String {
      val senderName:String = creators[bot.id] ?: error("Not creator found")
      val reply = """
            ${bot.id} 需要进行账户安全认证
            该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题
            完成以下账号认证即可成功登录|理论本认证在mirai每个账户中最多出现1次
            请将该链接在浏览器中打开并完成认证
            成功后输入/forward captcha ${bot.id}
        """.trimIndent()
      val sender = getCommandSender(senderName)
      val message = makeHoverClickUrl("账号认证验证链接", url)
      if (url.isBlank()) error("账号认证验证链接为空")
      bot.logger.info(url)

      sender.sendMessage(reply)
      sender.sendMessage(message)

      //需要帐号认证 通知登录命令的发送者
      val captcha = bot.captchaChannel.receive()
      sender.sendMessage("接收到验证码 $captcha")
      return captcha
   }
}

class MiraiLogger(di: DI, override val identity: String?) : MiraiLoggerPlatformBase(){
   private val logger: Logger by di.instance()
   override fun debug0(message: String?, e: Throwable?) {
      //logger.log(Level.WARNING,identity+message,e)
   }

   override fun error0(message: String?, e: Throwable?) {
      logger.log(Level.SEVERE, identity + message, e)
   }

   override fun info0(message: String?, e: Throwable?) {
      //logger.log(Level.INFO,identity+message,e)
   }

   override fun verbose0(message: String?, e: Throwable?) {
      //logger.log(Level.INFO,identity+message,e)
   }

   override fun warning0(message: String?, e: Throwable?) {
      logger.log(Level.WARNING,identity+message,e)
   }

}