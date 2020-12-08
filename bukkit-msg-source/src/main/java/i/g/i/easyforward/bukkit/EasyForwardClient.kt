package i.g.i.easyforward.bukkit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import i.g.i.easyforward.bukkit.extension.createWebSocket
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import org.bukkit.Bukkit
import org.kodein.di.DI
import org.kodein.di.instance

val objectMapper = ObjectMapper().registerModule(KotlinModule())

class EasyForwardClient(
   private val address:String,
   private val port:Int,
   di:DI
) {
   private val eventBus by di.instance<EventBus>()
   private val httpClient by di.instance<HttpClient>()
   private var wsClient :WebSocket? = null

   private fun WebSocket.registerFrameHandler(): WebSocket = frameHandler{ frame ->
      eventBus.publish(Address.Receive,frame.textData())
   }

   suspend fun onEnable(){
      wsClient = httpClient.createWebSocket(port,address,"/source")
      wsClient!!.registerFrameHandler()
      eventBus.consumer<String>(Address.Send){ msg ->
         wsClient!!.writeFinalTextFrame(msg.body())
      }
   }
}