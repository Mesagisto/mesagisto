package io.github.itsusinn.extension.vertx.eventloop

import io.github.itsusinn.extension.log.logger
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object EventBus

val vertx =
   Vertx.vertx().exceptionHandler{ EventBus.logger.error(it.message,it) }

val httpClient = vertx.createHttpClient()
val eventBus = vertx.eventBus()

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