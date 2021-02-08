package io.itsusinn.forward.bukkit

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import io.itsusinn.extension.base64.base64
import io.itsusinn.extension.base64.debase64
import io.itsusinn.forward.bukkit.extension.launch
import io.itsusinn.forward.client.warp
import io.ktor.client.* // ktlint-disable no-wildcard-imports
import io.ktor.client.features.websocket.* // ktlint-disable no-wildcard-imports
import io.ktor.http.* // ktlint-disable no-wildcard-imports
import io.ktor.http.cio.websocket.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import mu.KotlinLogging
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MessageForwardPlugin : JavaPlugin() {
   private val client = HttpClient { install(WebSockets) }
   private val logger = KotlinLogging.logger { }

   override fun onEnable() {

      server.getPluginCommand("forward")?.setExecutor(this)
      server.pluginManager.registerSuspendingEvents(ChatEventListener, this)

      saveDefaultConfig()
      reloadConfig()

      val startSignal = config.getInt("startSignal", 1)
      val host = config.getString("host")
      val port = config.getInt("port", -1)
      val address = config.getString("address")
      val token = config.getString("token")
      val name = config.getString("name", "unknown_name")!!

      ChatEventListener.chatEventHandler {
         if (startSignal != 0) {
            logger.warn { "Configuration value:[startSignal] isn't zero,plugin won't be enabled" }
            return@chatEventHandler
         }
         if (host == null ||
            port == -1 ||
            address == null ||
            token == null
         ) {
            logger.error { "Incomplete configuration file parameters" }
            return@chatEventHandler
         }

         val ws = addressWsMapper.getOrPut(address) {
            client.webSocketSession(
               HttpMethod.Get,
               host,
               port,
               "/ws?address=${address.base64}&token=${token.base64}&name=${name.base64}"
            ).warp().textFrameHandler {
               Bukkit.broadcastMessage(it.readText().debase64 ?: "err when debasing message")
            }.closeHandler {
               addressWsMapper.remove(address)
            }
         }

         ws.send("${it.player.name}:${it.message}".base64)
      }
      logger.info { "Successfully enabled message forward plugin" }
   }

   override fun onDisable() {
      ChatEventListener.chatEventHandler { }
      GlobalScope.launch {
         addressWsMapper.forEach {
            logger.info { "Closing websocket connection" }
            it.value.close()
            it.value.cancel()
         }
         addressWsMapper.clear()
      }
      logger.info { "Successfully disabled message forward plugin" }
   }
}
