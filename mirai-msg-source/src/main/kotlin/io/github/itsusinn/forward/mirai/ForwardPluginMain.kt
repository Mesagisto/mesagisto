package io.github.itsusinn.forward.mirai

import io.github.itsusinn.extension.forward.WebForwardClient
import io.vertx.core.http.WebSocket
import kotlinx.coroutines.async
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
import java.util.concurrent.ConcurrentHashMap

object ForwardPluginMain : KotlinPlugin(
   JvmPluginDescription(
      id = "io.github.itsusinn.forward",
      name = "ForwardPlugin",
      version = "0.1.0-rc1"
   )
){
   //target

   val eventChannel = globalEventChannel()
   val cacheBot = HashMap<Long,ArrayList<Bot>>()

   override fun onEnable() {
      ForwardConfig.reload()
      if (ForwardConfig.startSignal != 0) return
      launch {
         val wsClient = WebForwardClient(
            ForwardConfig.port,
            ForwardConfig.host,
            ForwardConfig.uri,
            ForwardConfig.appID,
            ForwardConfig.channelID,
            ForwardConfig.token
         )
         wsClient.frameHandler { frame ->
            val botList = cacheBot[ForwardConfig.target] ?: return@frameHandler
            val group = botList.random().getGroup(ForwardConfig.target) ?: return@frameHandler
            launch {
               group.sendMessage(frame.textData())
            }
         }
         eventChannel.subscribeAlways<GroupMessageEvent> {
            if (group.id != ForwardConfig.target) return@subscribeAlways
            val botList = cacheBot.getOrPut(group.id){ ArrayList<Bot>() }
            botList.add(bot)
            if (botList.size == 1){
               wsClient.writeFinalTextFrame("$senderName : ${message.contentToString()}")
            }
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

