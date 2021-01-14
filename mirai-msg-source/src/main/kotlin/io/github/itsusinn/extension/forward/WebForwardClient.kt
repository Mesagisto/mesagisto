package io.github.itsusinn.extension.forward

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.vertx.eventloop.vertx
import io.github.itsusinn.extension.vertx.websocket.AliveWebSocket
import io.vertx.core.http.WebSocket
import io.vertx.core.json.JsonObject

suspend fun WebForwardClient(
   port:Int = 1431,
   host:String = "127.0.0.1",
   uri:String = "/ws",
   appID:String = "test_app_id",
   channelID:String = "test_channel_id",
   token:String = "test_token_id",
): WebSocket {
   val para = JsonObject()
   para
      .put("app_id",appID)
      .put("channel_id",channelID)
      .put("token",token)
   return AliveWebSocket.create(vertx,port, host,"$uri/${para.encode().base64}")
}

suspend fun WebForwardClient(
   address:String = "127.0.0.1:1431/ws",
   appID:String = "test_app_id",
   channelID:String = "test_channel_id",
   token:String = "test_token",
): WebSocket {
   //parse exact argus from address
   val host  = runCatching { address.substring(0, address.indexOf(":")) }.getOrElse { "127.0.0.1" }
   val port = runCatching { address.substring(address.indexOf(":")+1, address.indexOf("/")).toInt() }.getOrElse { 1431 }
   val uri = runCatching { address.substring(address.indexOf("/")) }.getOrElse { "/ws" }
   return WebForwardClient(port, host, uri, appID, channelID, token)
}