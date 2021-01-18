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
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import okhttp3.*
import io.github.itsusinn.forward.mirai.Config.startSignal
import io.github.itsusinn.forward.mirai.Config.targetAddressMapper


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

   override fun onEnable() {
      Config.reload()
      if (Config.startSignal != 0) return
      eventChannel.subscribeAlways<GroupMessageEvent> {
         if (!targetAddressMapper.contains(group.id)) return@subscribeAlways

         val address = targetAddressMapper.get(group.id)!!
         addressEntity.getOrPut(address){HashSet()}.add(group)

         wsKeeper.getOrPut(address){
            val token = addressTokenRepo.get(address) ?: return@subscribeAlways
            val name = "mirai-${group.id.toString()}"
            val path = "/ws?address=${address.base64}&token=${token.base64}&name=${name.base64}"

            client.webSocketSession(
               host = Config.host,
               port = Config.port,
               path = path
            ).warp().textFrameHandler{
               addressEntity.get(address)?.random()?.sendMessage(it.readText().debase64 ?: "error")
            }.closeHandler {
               wsKeeper.remove(address)
            }

         }.send("$senderName:${message.contentToString()}".base64)
      }
      registerCommand(ForwardCommand)

      logger.info { "Plugin enabled" }
   }

   override fun onDisable() {
      if (startSignal != 0){
         if (Config.startSignal < 0) return
         Config.startSignal--
         return
      }

      unregisterAllCommands(this)
      logger.info { "Plugin disabled" }
   }
}

