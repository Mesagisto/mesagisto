package io.github.itsusinn.extension.vertx.websocket

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketFrame
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private val logger = KotlinLogging.logger {  }
private const val pingText = "HeartBeat"
private val pingBuffer = Buffer.buffer(pingText)

class AliveWebSocket(
   private val vertx: Vertx,
   private val httpClient: HttpClient,
   private val port:Int,
   private val host:String,
   private val uri:String,
   private var wsClient:WebSocket,
   private val aliveTime: Long
):WebSocket by wsClient,CoroutineScope {
   override val coroutineContext: CoroutineContext = vertx.dispatcher()

   private var proxyFrameHandler: Handler<WebSocketFrame>? = null
   override fun frameHandler(handler: Handler<WebSocketFrame>?): WebSocket {
      proxyFrameHandler = handler
      return this
   }

   private var proxyCloseHandler: Handler<Void>? = null
   override fun closeHandler(handler: Handler<Void>?): WebSocket {
      proxyCloseHandler = handler
      return this
   }

   /**
    * 将代理handler注册到websocket实例中
    */
   private fun registerHandlers(){
      wsClient.frameHandler { frame ->
         if (frame.binaryData().equals(pingBuffer)) return@frameHandler
         proxyFrameHandler?.handle(frame)
      }
      wsClient.closeHandler {
         vertx.cancelTimer(timer)
         proxyCloseHandler?.handle(it)
      }
      wsClient.pongHandler { alive = now() }
   }
   init { registerHandlers() }

   private val deadTime = aliveTime*1.5

   private var alive = now()

   private fun isAlive():Boolean = if (now() - alive > deadTime) false else true

   /**
    * 定时器，用于
    */
   private val timer:Long = vertx.setPeriodic(aliveTime){
      writePing(pingBuffer)
      if (isAlive()) return@setPeriodic
      launch {
         logger.warn { "WebSocket isn't alive anymore,attempt reconnect" }
         var attempt:Int = 0
         while (true){
            try {
               reconnect()
            }catch (e:Throwable){
               attempt++
            }
            if (attempt == 3){
               close()
               break
            }
            if (isAlive()) break
         }
      }
   }

   private suspend fun reconnect(){
      wsClient = httpClient.createWebSocket(port,host,uri)
      registerHandlers()
      alive = now()
   }

   companion object:CoroutineScope {
      suspend fun create(
         vertx: Vertx,
         port:Int,
         host:String,
         uri:String,
         aliveTime:Long = 60*1000
      ): WebSocket {
         val httpClient = vertx.createHttpClient()
         val wsClient = httpClient.createWebSocket(port,host,uri)
         return AliveWebSocket(vertx,httpClient, port, host, uri, wsClient, aliveTime)
      }
      fun now() = System.currentTimeMillis()

      override val coroutineContext: CoroutineContext
         get() = EmptyCoroutineContext
   }
}