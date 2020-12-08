package i.g.i.easyforward.bukkit.extension

import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun HttpClient.createWebSocket(
   port:Int,
   host:String,
   requestURI:String
) = suspendCoroutine<WebSocket>{ continuation ->
   webSocket(port,host,requestURI){
      if (it.failed()){
         continuation.resumeWithException(it.cause())
      } else {
         continuation.resume(it.result())
      }
   }
}