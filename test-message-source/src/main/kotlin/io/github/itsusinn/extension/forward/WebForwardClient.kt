package io.github.itsusinn.extension.forward

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.thread.CoroutineScopeWithDispatcher
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.extension.vertx.eventloop.vertx
import io.github.itsusinn.extension.vertx.httpclient.createWebSocket
import io.github.itsusinn.extension.vertx.httpclient.httpClient
import io.github.itsusinn.extension.vertx.websocket.pingBuffer
import io.vertx.core.Handler
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketFrame
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

private fun now() = System.currentTimeMillis()

class WebForwardClient private constructor(
   val port:Int,
   val host:String,
   val uri:String,
   val appID:String,
   val channelID:String,
   val token: String,
   val name: String,
   private val wsClient:WebSocket
):CoroutineScope by CoroutineScopeWithDispatcher(vertx.dispatcher()),
   WebSocket by wsClient {

   private var alive:Long = now()

   fun isAlive():Boolean = if (now() - alive > 60*1000*1.5) false else true

   private var proxyFrameHandler:Handler<WebSocketFrame>? = null
   init {
      wsClient.frameHandler { frame ->
         if (frame.binaryData().equals(pingBuffer)) return@frameHandler
         logger.debug { "Received: ${frame.textData()}" }
         proxyFrameHandler?.handle(frame)
      }
      vertx.setPeriodic(60*1000){
         wsClient.writePing(pingBuffer)
         logger.debug { "writing ping" }
      }
      wsClient.pongHandler {
         alive = now()
         logger.debug { "pongHandler received pong" }
      }

      vertx.setPeriodic(30*1000){
         logger.debug { "alive? ${isAlive()}" }
      }
   }
   override fun frameHandler(handler : Handler<WebSocketFrame>): WebSocket {
      proxyFrameHandler = handler
      return this
   }

   companion object Manager:SingleThreadCoroutineScope("forward-client"){

      /**
       * @param[port] WebSocket's port
       * @param[host] WebSocket's host
       * @param[uri] WebSocket's uri
       * @param[appID] Forward client's appID
       * @param[channelID] Forward client's channelID
       * @param[token] Forward client's token
       * @param[name] Forward client's source name
       */
      suspend fun create(
         port:Int = 1431,
         host:String = "127.0.0.1",
         uri:String = "/ws",
         appID:String = "test_app_id",
         channelID:String = "test_channel_id",
         token:String = "test_token_id",
         name:String = "test_name"
      ): WebForwardClient {
         val para = JsonObject()
         para
            .put("app_id",appID)
            .put("channel_id",channelID)
            .put("token",token)
         val wsClient:WebSocket
         try {
            wsClient = httpClient.createWebSocket(port, host, "$uri/${para.encode().base64}")
         } catch (e:Throwable){
            logger.error(e) { "Create ws client failed" }
            throw e
         }
         return WebForwardClient(port, host, uri, appID, channelID, token, name,wsClient)
      }
      /**
       * a short way of [create]
       */
      suspend fun create(
         address:String = "127.0.0.1:1431/ws",
         appID:String = "test_app_id",
         channelID:String = "test_channel_id",
         token:String = "test_token",
         name:String = "test_name"
      ): WebForwardClient {
         //parse exact argus from address
         val host  = kotlin.runCatching { address.substring(0, address.indexOf(":")) }.getOrElse { "127.0.0.1" }
         val port = kotlin.runCatching { address.substring(address.indexOf(":")+1, address.indexOf("/")).toInt() }.getOrElse { 1431 }
         val uri = kotlin.runCatching { address.substring(address.indexOf("/")) }.getOrElse { "/ws" }
         return create(port, host, uri, appID, channelID, token, name)
      }

   }
}