package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.logger
import io.github.itsusinn.extension.vertx.parsePath
import io.github.itsusinn.forward.dispatcher.data.PathArgu
import io.github.itsusinn.forward.dispatcher.repo.ConnectionMapper
import io.github.itsusinn.forward.dispatcher.repo.checkToken
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle

class DispatcherVerticle: CoroutineVerticle() {
   override suspend fun start(){
      val options = HttpServerOptions().apply {
         logActivity = true
         maxWebSocketFrameSize = 100
      }
      val server = vertx.createHttpServer(options)
      initWebsocket(server)
      server.listen(1431)
      logger.info("DispatcherVerticle Start On ws://127.0.0.1:1431")
   }
}

val connMapper = ConnectionMapper()

fun CoroutineVerticle.initWebsocket(server:HttpServer):HttpServer = server.webSocketHandler { ws ->

   if (!ws.path().startsWith("/ws")) ws.reject()

   val pathArgu:PathArgu
   try {
      pathArgu = parsePath<PathArgu>(ws.path().substring(3))
   }catch (e:Exception){
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

   ws.registerFrameHandler(address)
   ws.registerCloseHandler()
   ws.accept()
}

/**
 * register the handler of frame
 */
fun ServerWebSocket.registerFrameHandler(address: String) = frameHandler{ frame ->
   val textData = frame.textData()
   logger.debug("Receive from $address content: $textData")
   val currID = binaryHandlerID()
   //Forward a message to every ws connection
   // that is not currently sending messages to the server
   for (wsID in connMapper.findIdListByAddress(address)) {
      if (currID == wsID) continue
      connMapper.findInstanceById(wsID)?.writeFinalTextFrame(textData)
   }
}
/**
 * register the handler of ws close
 */
fun ServerWebSocket.registerCloseHandler()
= closeHandler { connMapper.closeByInstance(this) }
