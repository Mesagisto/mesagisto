package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.forward.dispatcher.extension.logger
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle

val connMapper = HashMap<String, ServerWebSocket>()
class DispatcherVerticle: CoroutineVerticle() {

   override suspend fun start(){

      val options = HttpServerOptions().apply {
         logActivity = true
         maxWebSocketFrameSize = 100
      }

      val server = vertx.createHttpServer(options)

      initWebsocket(server)
      server.listen(1431)
      logger.info("Server Start On ws://127.0.0.1:1431")
   }

   private fun initWebsocket(server:HttpServer):HttpServer = server.webSocketHandler{ ws ->
      // 获取每一个链接的ID
      val id: String = ws.binaryHandlerID()
      // 判断当前连接的ID是否存在于map集合中，如果不存在则添加进map集合中
      if (!isExists(id)) connMapper[id] = ws
      if (ws.path() != "/source") ws.reject()
      ws.frameHandler { frame ->

         val textData: String = frame.textData()
         val currID: String = ws.binaryHandlerID()
         //给非当前连接到服务器的每一个WebSocket连接发送消息
         for ((swsID, sws) in connMapper) {

            if (currID == swsID) continue

            sws.writeFinalTextFrame(textData)
         }
      }
      ws.closeHandler {
         connMapper.remove(id)
      }
   }

   private fun isExists(id: String): Boolean
      = connMapper.containsKey(id);

}