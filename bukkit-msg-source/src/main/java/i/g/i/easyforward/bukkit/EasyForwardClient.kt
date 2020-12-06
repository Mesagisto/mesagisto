package i.g.i.easyforward.bukkit

import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import org.kodein.di.DI
import org.kodein.di.instance

class EasyForwardClient(address:String,port:Int,di:DI) {
   private val eventBus: EventBus by di.instance()
   private val httpClient by di.instance<HttpClient>()
   private lateinit var wsClient :WebSocket
   init {
      httpClient.webSocket(port,address,"/source"){
         wsClient = it.result() ?: throw ConnectFailedException()
         wsClient.registerFrameHandler()
         eventBus.consumer<String>("out"){ msg ->
            wsClient.writeFinalTextFrame(msg.body())
         }
      }
   }
   private fun WebSocket.registerFrameHandler(): WebSocket = frameHandler{ frame ->
      eventBus.publish("in",frame.textData())
   }

}