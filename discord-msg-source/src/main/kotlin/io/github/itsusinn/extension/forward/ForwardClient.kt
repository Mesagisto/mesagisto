package io.github.itsusinn.extension.forward

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.log.logger
import io.github.itsusinn.extension.vertx.eventloop.createWebSocket
import io.github.itsusinn.extension.vertx.eventloop.eventBus
import io.github.itsusinn.extension.vertx.eventloop.httpClient
import io.github.itsusinn.extension.vertx.eventloop.vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.WebSocket
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.runBlocking

class ForwardClient private constructor(
   var port:Int,
   var host:String,
   var uri:String,
   var appID:String,
   var channelID:String,
   var token: String
) {
   var wsClient:WebSocket? = null
   private val listener = eventBus.consumer<String>("forward.source")

   init {
      listener.handler{
         wsClient?.writeFinalTextFrame(it.body())
      }
   }

   fun start() = runBlocking<Boolean> {
      val para = JsonObject()
      para
         .put("app_id",appID)
         .put("channel_id",channelID)
         .put("token",token)
      try {
         wsClient = httpClient.createWebSocket(port,host,"$uri/${para.encode().base64}")
         wsClient?.frameHandler {
            eventBus.publish("forward.server",it.textData())
         }
         return@runBlocking true
      } catch (t:Throwable){
         logger.error("Create ws client failed",t)
         return@runBlocking false
      }
   }

   fun close(){
      wsClient?.close()
   }

   companion object Factory{
      fun create(
         address:String = "127.0.0.1:1431/ws",
         appID:String = "test_app_id",
         channelID:String = "test_channel_id",
         token:String = "test_token"
      ): ForwardClient {

         val host  = kotlin.runCatching {
            address.substring(0,address.indexOf(":")) }.getOrElse { "127.0.0.1" }

         val port = kotlin.runCatching {
            address.substring(address.indexOf(":")+1,address.indexOf("/")).toInt() }.getOrElse { 1431 }

         val uri = kotlin.runCatching {
            address.substring(address.indexOf("/")) }.getOrElse { "/ws" }

         return create(port, host, uri, appID, channelID, token)
      }
      fun create(
         port:Int = 1431,
         host:String = "127.0.0.1",
         uri:String = "/ws",
         appID:String = "test_app_id",
         channelID:String = "test_channel_id",
         token:String = "test_token_id"
      ): ForwardClient {
         return ForwardClient(port, host, uri, appID, channelID, token)
      }
   }
}