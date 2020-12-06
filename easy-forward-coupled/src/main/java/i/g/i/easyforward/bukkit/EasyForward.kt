package i.g.i.easyforward.bukkit

import com.github.shynixn.mccoroutine.minecraftDispatcher
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import i.g.i.easyforward.bukkit.extension.KotlinPlugin
import i.g.i.easyforward.bukkit.extension.broadcastMessage
import i.g.i.easyforward.bukkit.extension.makeHoverClickUrl
import i.g.i.easyforward.bukkit.extension.CaptchaSolver
import i.g.i.easyforward.bukkit.service.*
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.*
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

class EasyForward : KotlinPlugin() {

   private val vertx: Vertx by lazy { Vertx.vertx() }

   private val di = DI {
      //vertx
      bind<Vertx>() with singleton { this@EasyForward.vertx }
      bind<EventBus>() with singleton { vertx.eventBus() }
      //coroutine
      bind<CoroutineContext>("sync") with singleton { this@EasyForward.minecraftDispatcher }
      bind<CoroutineContext>("async") with singleton { this@EasyForward.coroutineContext }

      bind<ConfigService>() with singleton { ConfigService(di) }
      bind<BotLoginService>() with singleton { BotLoginService(di) }
      bind<BotDispatcher>() with singleton { BotDispatcher(di) }
      bind<CaptchaSolver>() with singleton { CaptchaSolver(di) }
      bind<StorageService>() with singleton { StorageService(di) }

      bind<MessageListener>() with singleton { MessageListener(di) }
      bind<ForwardCommandExecutor>() with singleton { ForwardCommandExecutor(di) }

      bind<KotlinPlugin>() with singleton { this@EasyForward }

      bind<Logger>() with singleton { this@EasyForward.logger }

      bind<ImageUploadService>() with singleton { ImageUploadService(di) }
   }

   private val configService: ConfigService by di.instance()
   private val botDispatcher: BotDispatcher by di.instance()

   private val messageListener: MessageListener by di.instance()
   private val forwardCommandExecutor: ForwardCommandExecutor by di.instance()

   override fun onEnable() {

      logger.fine("EasyForward is Loading")
      logger.fine("GitHub: https://github.com/Itsusinn/easy-forward")

      configService.load()
      //注册消息监听器
      server.pluginManager.registerSuspendingEvents(messageListener, this)
      logger.fine("Register listener successfully")
      //注册命令处理器
      server.getPluginCommand("forward")!!.setSuspendingExecutor(forwardCommandExecutor)

      logger.fine("Register command executor successfully")
   }

   override fun onDisable() {
      //保存配置
      configService.save()
   }

   init {
      GlobalScope.launch{
         subscribeGroupMessages {
            sentFrom(botDispatcher.getTarget()).invoke {
               //只接受listener收到的消息
               if (bot.id != botDispatcher.getListener()) return@invoke
               //防止转发speaker发送的消息
               botDispatcher.speakers.forEach {
                  if (it.id == sender.id) return@invoke
               }

               if (message.firstOrNull(PlainText) != null) {
                  val textMessage = message.first(PlainText).content
                  if (textMessage.startsWith("online")
                     || textMessage.startsWith("在线")
                     || textMessage.startsWith("zx")
                  ) {
                     val players = Bukkit.getOnlinePlayers()
                     reply("当前有${players.size}个玩家在线")
                     var playerList = ""
                     players.forEach {
                        playerList += "${it.name}\n"
                     }
                     if (playerList != "") {
                        reply(playerList.dropLast(1))
                        return@invoke
                     }
                  }
                  broadcastMessage("<${sender.nameCardOrNick}> $textMessage")
               }
               if (message.firstOrNull(Image) != null) {
                  val complexMessage = TextComponent("<${this.sender.nameCardOrNick}> ")
                  complexMessage.addExtra(makeHoverClickUrl("[ImageLink]", message.first(Image).url()))
                  broadcastMessage(complexMessage)
               }
            }
         }
      }
   }
}