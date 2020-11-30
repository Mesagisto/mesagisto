package org.meowcat.minecraft.forward

import com.github.shynixn.mccoroutine.minecraftDispatcher
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
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
import org.meowcat.minecraft.forward.kotlin.KotlinPlugin
import org.meowcat.minecraft.forward.mirai.CaptchaSolver
import org.meowcat.minecraft.forward.service.BotLoginService
import org.meowcat.minecraft.forward.service.ConfigService
import org.meowcat.minecraft.forward.service.ImageUploadService
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

class Forward : KotlinPlugin() {

   private val di = DI{
      bind<CoroutineContext>("minecraft") with singleton { this@Forward.minecraftDispatcher }
      bind<CoroutineContext>("async") with singleton { this@Forward.coroutineContext }

      bind<ConfigService>() with singleton { ConfigService(di) }
      bind<BotLoginService>() with singleton { BotLoginService(di) }
      bind<BotDispatcher>() with singleton { BotDispatcher(di) }
      bind<CaptchaSolver>() with singleton { CaptchaSolver(di) }

      bind<MessageListener>() with singleton { MessageListener(di) }
      bind<ForwardCommandExecutor>() with singleton { ForwardCommandExecutor(di) }

      bind<KotlinPlugin>() with singleton { this@Forward }

      bind<Logger>() with singleton { this@Forward.logger }

      bind<ImageUploadService>() with singleton { ImageUploadService(di) }
   }

   private val configService:ConfigService by di.instance()
   private val botDispatcher:BotDispatcher by di.instance()

   private val messageListener:MessageListener by di.instance()
   private val forwardCommandExecutor:ForwardCommandExecutor by di.instance()

   override fun onEnable(){

      logger.info("Forward is Loading")
      logger.info("GitHub: https://github.com/Itsusinn/minecraft-message-forward")

      configService.load()

      //注册消息监听器
      server.pluginManager.registerSuspendingEvents(messageListener, this)
      logger.info("Register listener successfully")
      //注册命令处理器
      server.getPluginCommand("forward")!!.setSuspendingExecutor(forwardCommandExecutor)

      logger.info("Register command executor successfully")
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
               if (bot.id!= botDispatcher.getListener()) return@invoke
               //防止转发speaker发送的消息
               botDispatcher.speakers.forEach {
                  if (it.id == sender.id) return@invoke
               }
               if (message.isPlain()){
                  broadcastTextMessage("<${this.sender.nameCardOrNick}> ${message.content}")
                  return@invoke
               }
               if(message.firstOrNull(Image) != null){
                  val complexMessage = TextComponent("<${this.sender.nameCardOrNick}> ")
                  complexMessage.addExtra(makeClickUrl("图片",message.first(Image).url()))
                  broadcastComponentMessage(complexMessage)
                  return@invoke
               }
               if (message.contains(PokeMessage.Poke)){
                  val nums = Bukkit.getOnlinePlayers().size
                  reply(PlainText("$nums players are online"))
                  return@invoke
               }
            }
         }
      }
   }
}