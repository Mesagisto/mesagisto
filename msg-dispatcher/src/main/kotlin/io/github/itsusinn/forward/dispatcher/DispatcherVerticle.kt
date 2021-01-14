package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.vertx.parsePath
import io.github.itsusinn.extension.vertx.websocket.pingBuffer
import io.github.itsusinn.forward.dispatcher.data.PathArgu
import io.github.itsusinn.forward.dispatcher.repo.ConnectionRepo
import io.github.itsusinn.forward.dispatcher.repo.checkToken
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {  }

class DispatcherVerticle: CoroutineVerticle() {
   override suspend fun start(){
      val options = HttpServerOptions().apply {
         logActivity = true
         maxWebSocketFrameSize = 100
      }
      val server = vertx.createHttpServer(options)
      initWebsocket(server)
      server.listen(1431)
      logger.info{"DispatcherVerticle Start On ws://127.0.0.1:1431"}
   }
}

val connMapper = ConnectionRepo()

fun CoroutineVerticle.initWebsocket(server:HttpServer):HttpServer = server.webSocketHandler { ws ->

   if (!ws.path().startsWith("/ws")) ws.reject()

   val pathArgu = parsePath<PathArgu>(ws.path().substring(3)) ?: run {
      ws.reject()
      return@webSocketHandler
   }

   //check if this token is legal
   if (checkToken(pathArgu)) {
      ws.accept()
   } else {
      ws.reject()
      return@webSocketHandler
   }

   val address = "${pathArgu.appID}:${pathArgu.channelID}"
   connMapper.save(address,ws,ws.binaryHandlerID())

   ws
      .frameHandler { frame ->
         val currID = ws.binaryHandlerID()
         val data = frame.binaryData()
         if (data.equals(pingBuffer)){
            logger.debug { "Received a ping frame" }
            connMapper.reAliveById(currID)
            return@frameHandler
         }
         logger.debug{ "Receive from $address content:${frame.textData()}" }
         val idList = connMapper.findIdListByAddressWithNoCopy(address) ?: return@frameHandler

         //find other subscribers and send message to them
         for (id in idList){
            if (id == currID) continue
            connMapper.findInstanceById(id)?.writeFinalBinaryFrame(data)
         }

      }
      .closeHandler { connMapper.closeById(ws.binaryHandlerID()) }
      .accept()

   connMapper.setAutoClean(vertx)
}

