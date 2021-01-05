package io.github.itsusinn.forward.test

import io.github.itsusinn.extension.vertx.createWebSocket
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking

object App {
   @JvmStatic fun main(args:Array<String>) = runBlocking{
      val vertx = Vertx.vertx()
      val httpClient = vertx.createHttpClient()
      val address = readLine()!!// 127.0.0.1:1431/ws
      val host = address.substring(0,address.indexOf(":")-1)
      val port = address.substring(address.indexOf(":")+1,address.indexOf("/")-1).toInt()
      val uri = address.substring(address.indexOf("/")+1)
      val ws = httpClient.createWebSocket(port,host,uri)


   }
}