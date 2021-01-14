package io.github.itsusinn.extension.vertx.websocket

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketFrame
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun HttpClient.createWebSocket(
   port:Int,
   host:String,
   requestURI:String
) = suspendCoroutine<WebSocket> { continuation ->
   //vertx method is async
   //will truly suspend
   webSocket(port, host, requestURI) {
      if (it.failed()) {
         continuation.resumeWithException(it.cause())
      } else {
         continuation.resume(it.result())
      }
   }
}
