package io.github.itsusinn.extension.vertx.websocket

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.forward.dispatcher.repo.EndpointKeeper
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
import io.vertx.core.json.JsonObject
import java.util.concurrent.atomic.AtomicInteger

fun ServerWebSocket.warp(
   vertx: Vertx,
   keeper:EndpointKeeper,
   address: String,
   identifier: String = textHandlerID().base64,
):ServerWebSocket =
   WarpedWebSocket(vertx,this,keeper,address, identifier)

class WarpedWebSocket(
   private val vertx: Vertx,
   private val instance:ServerWebSocket,
   private val keeper: EndpointKeeper,
   private val address:String,
   private val identifier:String,
):ServerWebSocket by instance {
   private val eventBus = vertx.eventBus()
   private val consumer = eventBus.localConsumer<String>("local.$address")
   private var proxyFrameHandler:Handler<WebSocketFrame>? = null
   init {
      keeper.clientCounter.getOrPut(address){ AtomicInteger(0) }.incrementAndGet()

      keeper.sharedConsumer.getOrPut(address){
         eventBus.consumer<String>(address).handler {
            eventBus.publish("local.$address",it.body())
         }
      }

      consumer.handler { message ->
         val pack = JsonObject(message.body())

         val senderID = pack.getString("sender") ?: return@handler
         if (senderID == identifier) return@handler
         val data = pack.getString("data") ?: return@handler

         instance.writeFinalTextFrame(data)
      }
      instance.frameHandler {
         //TODO Ping-Pong
         val pack = JsonObject()
         pack.put("sender",identifier)
         pack.put("data",it.textData())
         eventBus.publish(address,pack.encode())
         proxyFrameHandler?.handle(it)
      }
   }

   @Deprecated("No need to use")
   override fun frameHandler(handler: Handler<WebSocketFrame>?): ServerWebSocket {
      proxyFrameHandler = handler
      return this
   }

   override fun close(): Future<Void> {
      consumer.unregister()
      val counter = keeper.clientCounter.get(address)!!
      val clients = counter.decrementAndGet()
      if (clients == 0){
         //clear if clients doesn't exit
         //TODO
      }
      return instance.close()
   }
}