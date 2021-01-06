package io.github.itsusinn.forward.test

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.logger
import io.github.itsusinn.extension.vertx.createWebSocket
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.runBlocking

object App {
   @JvmStatic fun main(args:Array<String>) = runBlocking{
      val vertx = Vertx.vertx()
      val httpClient = vertx.createHttpClient()
      logger.info("""plz input address such as "127.0.0.1:1431/ws".""")
      var address = readLine()
      if (address=="d") address = "127.0.0.1:1431/ws"
      address!!
      val host = address.substring(0,address.indexOf(":"))
      val port = address.substring(address.indexOf(":")+1,address.indexOf("/")).toInt()
      val uri = address.substring(address.indexOf("/"))
      val para = JsonObject()
      para
         .put("app_id","test_app_id")
         .put("channel_id","test_channel_id")
         .put("token","test_token")
      val ws = httpClient.createWebSocket(port,host,"$uri/${para.encode().base64}")
      logger.info("ws connect to $address successfully")
      ws.frameHandler {
         logger.info("Receive: ${it.textData()}")
      }
      var line:String
      while (true){
         line = readLine()!!
         if (line=="exit") break
         logger.info("Send: $line")
         ws.writeFinalTextFrame(line)
      }
      System.exit(0)
   }
}