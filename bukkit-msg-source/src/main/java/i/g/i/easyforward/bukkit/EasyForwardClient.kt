package i.g.i.easyforward.bukkit

import i.g.i.easyforward.bukkit.extension.createWebSocket
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class EasyForwardClient(
   private val address:String,
   private val port:Int,
   private val di:DI) {
   private val eventBus: EventBus by di.instance()
   private val httpClient by di.instance<HttpClient>()
   private lateinit var wsClient :WebSocket

   private fun WebSocket.registerFrameHandler(): WebSocket = frameHandler{ frame ->
      eventBus.publish(Address.In,frame.textData())
   }

   suspend fun onCreate(){
      wsClient = httpClient.createWebSocket(port,address,"/source")
      wsClient.registerFrameHandler()
      eventBus.consumer<String>(Address.Out){ msg ->
         wsClient.writeFinalTextFrame(msg.body())
      }
   }



}