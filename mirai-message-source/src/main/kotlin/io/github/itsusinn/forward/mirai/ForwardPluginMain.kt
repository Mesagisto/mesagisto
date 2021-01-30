package io.github.itsusinn.forward.mirai

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.base64.debase64
import io.github.itsusinn.extension.forward.client.warp
import io.github.itsusinn.forward.mirai.Config.addressTokenRepo
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.utils.io.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.registerCommand
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregisterAllCommands
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import okhttp3.*
import io.github.itsusinn.forward.mirai.Config.startSignal
import io.github.itsusinn.forward.mirai.Config.targetAddressMapper
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.internalId

object ForwardPluginMain : KotlinPlugin(
   JvmPluginDescription(
      id = "io.github.itsusinn.forward",
      name = "ForwardPlugin",
      version = "0.1.0-rc1"
   )
){
   val client = HttpClient {
      install(WebSockets)
   }

   val eventChannel = globalEventChannel()
   val listener = eventChannel.subscribeAlways<GroupMessageEvent> {
      if (!targetAddressMapper.contains(group.id)) return@subscribeAlways
      this.message.internalId

      val address = targetAddressMapper.get(group.id)!!
      addressEntity.getOrPut(address){ HashSet() }.add(group)

      wsKeeper.getOrPut(address){
            val token = addressTokenRepo.get(address) ?: return@subscribeAlways
            val name = "mirai-${group.id.toString()}"
            val path = "/ws?address=${address.base64}&token=${token.base64}&name=${name.base64}"

            client.webSocketSession(
               host = Config.host,
               port = Config.port,
               path = path
            ).warp().textFrameHandler{
               val msg = it.readText().debase64 ?: return@textFrameHandler
               addressEntity.get(address)?.random()?.sendMessage(msg)
            }.closeHandler {
               wsKeeper.remove(address)
            }

         }.send("$senderName:${message.contentToString()}".base64)
   }
   init {
      eventChannel.subscribeAlways<NewFriendRequestEvent> {
         accept()
      }
      eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
         accept()
      }
   }

   override fun onEnable() {
      Config.reload()
      if (Config.startSignal != 0) return

      registerCommand(ForwardCommand)

      logger.info { "Plugin enabled" }
   }

   override fun onDisable() {
      unregisterAllCommands(this)
      logger.info { "Plugin disabled" }
   }
}

