package i.g.i.easyforward.bukkit

import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import org.kodein.di.DI
import org.kodein.di.instance

class EasyForwardClient(di:DI) {
   private val eventBus: EventBus by di.instance()
   private val httpClient by di.instance<HttpClient>()
   private lateinit var wsClient :WebSocket
   init {
      httpClient
         .webSocket(1431,"127.0.0.1","/source"){
            wsClient = it.result() ?: error("Null WsClient")
            wsClient.frameHandler { frame->
               eventBus.publish("in",frame.textData())
            }
            eventBus.consumer<String>("out"){ msg ->
               wsClient.writeFinalTextFrame(msg.body())
            }
         }
   }
}