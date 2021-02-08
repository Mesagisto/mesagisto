package itsusinn.extension.vertx.websocket

import io.itsusinn.extension.base64.base64
import io.itsusinn.extension.base64.debase64
import io.itsusinn.extension.md5.md5
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
import io.vertx.core.json.JsonObject
import itsusinn.forward.dispatcher.repo.EndpointKeeper
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger { }
private fun now() = System.currentTimeMillis()

/**
 * 包裹到[WarpedWebSocket]
 */
fun ServerWebSocket.warp(
   vertx: Vertx,
   keeper: EndpointKeeper,
   address: String,
   name: String?
): ServerWebSocket {
   logger.debug { "connect ws ${binaryHandlerID().md5.base64}" }
   return WarpedWebSocket(vertx, this, keeper, address, binaryHandlerID().md5.base64, name ?: "unknown name")
}

/**
 * Feat:
 * KeepAlive.
 * Own a publisher on "address",
 * and a localConsumer on "local.$address".
 *
 * @param[address]用户地址
 */
class WarpedWebSocket(
   private val vertx: Vertx,
   private val instance: ServerWebSocket,
   private val keeper: EndpointKeeper,
   private val address: String,
   private val identifier: String,
   private val name: String,
) : ServerWebSocket by instance {
   private var last = now()
   fun isAlive(): Boolean = now() - last < 90 * 1000

   private val eventBus = vertx.eventBus()
   private val consumer = eventBus.localConsumer<String>("local.$address")
   private var proxyFrameHandler: Handler<WebSocketFrame>? = null
   init {
      keeper.clientCounter.getOrPut(address) { AtomicInteger(0) }.incrementAndGet()

      keeper.sharedConsumer.getOrPut(address) {
         eventBus.consumer<String>(address).handler {
            logger.debug { "received at shared consumer content:${it.body()}" }
            logger.debug { "pushing to local consumer" }
            eventBus.publish("local.$address", it.body())
         }
      }

      consumer.handler { message ->
         val pack = JsonObject(message.body())

         val senderID = pack.getString("sender") ?: return@handler
         if (senderID == identifier) return@handler
         val data = pack.getString("data") ?: return@handler
         logger.debug { "writing to $name content:$data" }
         instance.writeFinalTextFrame(data)
      }

      instance.frameHandler {
         last = now()
         if (it.textData() == pingText) {
            logger.debug { "Received ping-frame from $name" }
            writeFinalTextFrame(pingText)
            return@frameHandler
         }

         val data = it.textData()
         val pack = JsonObject()
         pack.put("sender", identifier)
         pack.put("data", data)
         eventBus.publish(address, pack.encode())
         logger.debug { "receive from ws $name content:${it.textData().debase64}" }
         proxyFrameHandler?.handle(it)
      }

      vertx.setPeriodic(72 * 1000) {
         if (!isAlive()) {
            logger.debug { "websocket:$name doesn't alive any more" }
            vertx.cancelTimer(it)
            close()
         }
      }
   }

   @Deprecated("No need to use")
   override fun frameHandler(handler: Handler<WebSocketFrame>?): ServerWebSocket {
      proxyFrameHandler = handler
      return this
   }

   override fun close(): Future<Void> {
      logger.debug { "closing websocket $name" }
      consumer.unregister()
      val counter = keeper.clientCounter.get(address)!!
      val clients = counter.decrementAndGet()
      if (clients == 0) {
         keeper.clientCounter.remove(address)
         keeper.sharedConsumer.remove(address)?.unregister()
      }
      return instance.close()
   }
}
