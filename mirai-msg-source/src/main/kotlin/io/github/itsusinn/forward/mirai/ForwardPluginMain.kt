package io.github.itsusinn.forward.mirai

import io.github.itsusinn.extension.forward.data.FrameData
import io.github.itsusinn.extension.forward.data.TextMessage
import io.github.itsusinn.extension.forward.data.textMessage
import io.github.itsusinn.extension.forward.data.warp
import io.github.itsusinn.extension.jackson.asString
import io.github.itsusinn.extension.jackson.asStringOrFail
import io.github.itsusinn.extension.jackson.readValue
import io.github.itsusinn.extension.jackson.readValueOrFail
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.utils.io.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.registerCommand
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregisterAllCommands
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.BotReloginEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import java.lang.StringBuilder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import okhttp3.*
import okio.ByteString
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

object ForwardPluginMain : KotlinPlugin(
   JvmPluginDescription(
      id = "io.github.itsusinn.forward",
      name = "ForwardPlugin",
      version = "0.1.0-rc1"
   )
){
   val client = HttpClient {
      install(WebSockets) {
         pingInterval = -1
         maxFrameSize = Long.MAX_VALUE
      }
   }

   val eventChannel = globalEventChannel()
   val cacheBot = HashMap<Long,HashSet<Bot>>()

   override fun onEnable() {
      ForwardConfig.reload()
      if (ForwardConfig.startSignal != 0) return
      launch {
         val path:String
         ForwardConfig.apply {
            path = "$uri?app_id=$appID&channel_id=$channelID&token=$token"
         }

         val ws = client.webSocketSession(
            host = ForwardConfig.host,
            port = ForwardConfig.port,
            path = path
         ).warp()

         eventChannel.subscribeAlways<GroupMessageEvent> {
            if (group.id != ForwardConfig.target) return@subscribeAlways
            val botList = cacheBot.getOrPut(group.id){ HashSet<Bot>() }
            botList.add(bot)
            if (botList.size == 1){
               ws.send("$senderName:${message.contentToString()}")
            }
         }

         ws.textFrameHandler {
            val botList = cacheBot[ForwardConfig.target] ?: return@textFrameHandler
            val group = botList.random().getGroup(ForwardConfig.target) ?: return@textFrameHandler
            group.sendMessage(it.readText())
         }
      }
      registerCommand(ForwardCommand)

      logger.info { "Plugin enabled" }
   }

   override fun onDisable() {
      if (ForwardConfig.startSignal != 0){
         if (ForwardConfig.startSignal < 0) return
         ForwardConfig.startSignal--
         return
      }

      unregisterAllCommands(this)
      logger.info { "Plugin disabled" }
   }
}

