package io.github.itsusinn.forward.dispatcher

import io.github.itsusinn.extension.logger
import io.github.itsusinn.extension.vertx.parsePath
import io.github.itsusinn.forward.dispatcher.data.PathArgu
import io.github.itsusinn.forward.dispatcher.data.Source
import io.github.itsusinn.forward.dispatcher.repo.checkToken
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlin.concurrent.thread

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
}

/**
 * @property identifierAddressMapper <identifier> - <address>
 * @property identifierInstanceMapper <identifier> - <ws instance>
 * @property addressIdentifierMapper <address> - <list of identifiers>
 */
class ConnectionMapper{
   private val identifierAddressMapper = HashMap<String, String>()
   private val identifierInstanceMapper = HashMap<String, ServerWebSocket>()
   private val instanceIdentifierMapper = HashMap<ServerWebSocket, String>()
   private val addressIdentifierMapper = HashMap<String, ArrayList<String>>()
   /**
    * @param address publish address,appended with "appID:channelID"
    * @param instance ws instance
    * @param identifier the id of ws,usually binaryHandlerID or hashcode
    */
   fun save(address: String,instance:ServerWebSocket,identifier: String){
      identifierAddressMapper[identifier] = address
      identifierInstanceMapper[identifier] = instance
      instanceIdentifierMapper[instance] = identifier
      addressIdentifierMapper.getOrPut(address){ ArrayList<String>() } + identifier
   }
   fun findAddressById(identifier: String):String {
      return identifierAddressMapper[identifier]
         ?: throw NullPointerException()
   }
   fun findIdByInstance(instance: ServerWebSocket): String {
      return instanceIdentifierMapper[instance]
         ?: throw NullPointerException()
   }
   fun findInstanceById(identifier:String): ServerWebSocket {
      return identifierInstanceMapper[identifier]
         ?: throw NullPointerException("Cannot get the ws instance of specific id")
   }
   fun findIdListByAddress(address:String): List<String> {
      return addressIdentifierMapper[address]
         ?: throw NullPointerException("Cannot get the ws id list of specific address")
   }
   fun closeByInstance(ws:ServerWebSocket) {
      val id = instanceIdentifierMapper.remove(ws)
         ?: throw NullPointerException("Cannot get the ws id of specific instance")
      identifierInstanceMapper.remove(id,ws)
      val address = identifierAddressMapper.remove(id)
         ?: throw NullPointerException("Cannot get the ws address of specific id")
      val wsIDList = addressIdentifierMapper[address]
         ?: throw NullPointerException("Cannot get the ws id list of specific address")
      wsIDList.remove(id)
      if (wsIDList.isEmpty()) addressIdentifierMapper.remove(address)
   }
}

val connMapper = ConnectionMapper()

fun CoroutineVerticle.initWebsocket(server:HttpServer):HttpServer = server.webSocketHandler { ws ->

   if (ws.path().startsWith("/ws")) ws.reject()

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
   ws.registerCloseHandler(connMapper)
}

/**
 * register the handler of frame
 */
fun ServerWebSocket.registerFrameHandler(address: String) = frameHandler{ frame ->
   val textData = frame.binaryData()
   val currID = binaryHandlerID()
   //Forward a message to every ws connection
   // that is not currently sending messages to the server
   for (wsID in connMapper.findIdListByAddress(address)) {
      if (currID == wsID) continue
      connMapper.findInstanceById(wsID).writeFinalBinaryFrame(textData)
   }
}
/**
 * register the handler of ws close
 */
fun ServerWebSocket.registerCloseHandler(connMapper:ConnectionMapper) = closeHandler {
   connMapper.closeByInstance(this)
}
