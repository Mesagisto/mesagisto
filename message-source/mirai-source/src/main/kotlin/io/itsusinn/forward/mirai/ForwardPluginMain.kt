package io.itsusinn.forward.mirai

import io.itsusinn.extension.base64.base64
import io.itsusinn.extension.base64.debase64
import io.itsusinn.forward.client.warp
import io.itsusinn.forward.mirai.ForwardConfig.addressTokenRepo
import io.itsusinn.forward.mirai.ForwardConfig.targetAddressMapper
import io.ktor.client.* // ktlint-disable no-wildcard-imports
import io.ktor.client.features.websocket.* // ktlint-disable no-wildcard-imports
import io.ktor.http.* // ktlint-disable no-wildcard-imports
import io.ktor.http.cio.websocket.* // ktlint-disable no-wildcard-imports
import io.ktor.utils.io.* // ktlint-disable no-wildcard-imports
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.registerCommand
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregisterAllCommands
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.* // ktlint-disable no-wildcard-imports
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.internalId
import net.mamoe.mirai.utils.info
import okhttp3.* // ktlint-disable no-wildcard-imports

object ForwardPluginMain : KotlinPlugin(
   JvmPluginDescription(
      id = "io.github.itsusinn.forward",
      name = "ForwardPlugin",
      version = "0.1.1"
   )
) {
   val client = HttpClient {
      install(WebSockets)
   }

   val eventChannel = globalEventChannel()
   val listener = eventChannel.subscribeAlways<GroupMessageEvent> {
      if (!targetAddressMapper.contains(group.id)) return@subscribeAlways
      this.message.internalId

      val address = targetAddressMapper.get(group.id)!!
      addressEntity.getOrPut(address) { HashSet() }.add(group)

      wsKeeper.getOrPut(address) {
         val token = addressTokenRepo.get(address) ?: return@subscribeAlways
         val name = "mirai-${group.id}"
         val path = "/ws?address=${address.base64}&token=${token.base64}&name=${name.base64}"

         client.webSocketSession(
            host = ForwardConfig.host,
            port = ForwardConfig.port,
            path = path
         ).warp().textFrameHandler {
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
      ForwardConfig.reload()
      if (ForwardConfig.startSignal != 0) return

      registerCommand(ForwardCommand)

      logger.info { "Plugin enabled" }
   }

   override fun onDisable() {
      unregisterAllCommands(this)
      logger.info { "Plugin disabled" }
   }
}
