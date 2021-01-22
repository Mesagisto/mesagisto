package io.github.itsusinn.forward.bukkit

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.base64.debase64
import io.github.itsusinn.extension.forward.client.warp
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MessageForwardPlugin : JavaPlugin() {
   private val client = HttpClient { install(WebSockets) }
   private val logger = KotlinLogging.logger {  }

   override fun onEnable(){

      server.getPluginCommand("forward")?.setExecutor(this)
      server.pluginManager.registerSuspendingEvents(ChatEventListener,this)

      saveDefaultConfig()
      reloadConfig()

      val startSignal = config.getInt("startSignal",1)
      val host = config.getString("host")
      val port = config.getInt("port",-1)
      val address = config.getString("address")
      val token = config.getString("token")
      val name = config.getString("name","unknown_name")!!

      ChatEventListener.chatEventHandler {
         if (startSignal != 0){
            logger.warn { "Configuration value:[startSignal] isn't zero,plugin won't be enabled" }
            return@chatEventHandler
         }
         if( host == null
            || port == -1
            || address == null
            || token == null
         ){
            logger.error { "Incomplete configuration file parameters" }
            return@chatEventHandler
         }

         val ws = addressWsMapper.getOrPut(address){
            client.webSocketSession(
               HttpMethod.Get,
               host,
               port,
               "/ws?address=${address.base64}&token=${token.base64}&name=${name.base64}"
            ).warp().textFrameHandler {
               Bukkit.broadcastMessage(it.readText().debase64 ?:"err when debasing message")
            }
         }

         ws.send("${it.player.name}:${it.message}".base64)

      }
      logger.info { "Successfully enabled message forward plugin" }
   }

   override fun onDisable() {
      ChatEventListener.chatEventHandler{ }
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

