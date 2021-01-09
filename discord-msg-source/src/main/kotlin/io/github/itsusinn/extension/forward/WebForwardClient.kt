package io.github.itsusinn.extension.forward

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.log.logger
import io.github.itsusinn.extension.vertx.eventloop.eventBus
import io.github.itsusinn.extension.vertx.httpclient.createWebSocket
import io.github.itsusinn.extension.vertx.httpclient.httpClient
import io.vertx.core.http.WebSocket
import io.vertx.core.json.JsonObject

class WebForwardClient private constructor(
   val port:Int,
   val host:String,
   val uri:String,
   val appID:String,
   val channelID:String,
   val token: String,
   val name: String,
) {

   private lateinit var wsClient:WebSocket

   private val consumer by lazy {
      logger.info { "consumer added at forward.$name" }
      eventBus.localConsumer<String>("forward.$name")
   }
   private val publisher by lazy {
      logger.info { "publisher added at forward.source" }
      eventBus.publisher<String>("forward.source")
   }

   /**
    * must invoke after start()
    * or ws client frame handle cannot be registered
    */
   private fun initEventBus(){
      consumer.handler{
         wsClient.writeFinalTextFrame(it.body())
      }
      wsClient.frameHandler {
         publisher.write(it.textData())
      }
   }
   suspend fun link() {
      val para = JsonObject()
      para
         .put("app_id",appID)
         .put("channel_id",channelID)
         .put("token",token)
      try {
         wsClient = httpClient.createWebSocket(port,host,"$uri/${para.encode().base64}")
         initEventBus()
      } catch (t:Throwable){
         logger.error(t) { "Create ws client failed" }
         consumer.unregister()
         publisher.close()
         throw t
      }
   }

   fun resume(){
      wsClient.resume()
      consumer.resume()
   }

   fun close(){
      wsClient.pause()
      consumer.pause()
   }
   fun stop(){
      wsClient.close()
      consumer.unregister()
      publisher.close()
   }

   companion object Factory{
      /**
       * a short way of [createFully]
       */
      fun create(
         address:String = "127.0.0.1:1431/ws",
         appID:String = "test_app_id",
         channelID:String = "test_channel_id",
         token:String = "test_token",
         name:String = "test_name"
      ): WebForwardClient {

         val host  = kotlin.runCatching {
            address.substring(0,address.indexOf(":")) }.getOrElse { "127.0.0.1" }

         val port = kotlin.runCatching {
            address.substring(address.indexOf(":")+1,address.indexOf("/")).toInt() }.getOrElse { 1431 }

         val uri = kotlin.runCatching {
            address.substring(address.indexOf("/")) }.getOrElse { "/ws" }

         return createFully(port, host, uri, appID, channelID, token,name)
      }

      /**
       * @param[port] WebSocket's port
       * @param[host] WebSocket's host
       * @param[uri] WebSocket's uri
       * @param[appID] Forward client's appID
       * @param[channelID] Forward client's channelID
       * @param[token] Forward client's token
       * @param[name] Forward client's source name
       */
      fun createFully(
         port:Int = 1431,
         host:String = "127.0.0.1",
         uri:String = "/ws",
         appID:String = "test_app_id",
         channelID:String = "test_channel_id",
         token:String = "test_token_id",
         name:String = "test_name"
      ): WebForwardClient {
         return WebForwardClient(port, host, uri, appID, channelID, token,name)
      }
   }
}