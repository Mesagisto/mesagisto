package io.github.itsusinn.forward.test

import io.github.itsusinn.extension.base64.base64
import io.github.itsusinn.extension.config.ConfigKeeper
import io.github.itsusinn.extension.forward.WebForwardClient
import io.github.itsusinn.extension.runtime.addShutdownHook
import io.github.itsusinn.extension.runtime.exit
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.extension.vertx.eventloop.eventBus
import io.github.itsusinn.extension.vertx.eventloop.vertx
import io.github.itsusinn.extension.vertx.httpclient.httpClient
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.io.File

object App: SingleThreadCoroutineScope("forward-test") {
   //Make sure the forward folder exists
   init { File("forward").apply { mkdir() } }

   private val configKeeper = ConfigKeeper
      .create<TestConfigData>(
         defaultConfig, File("forward/test.json")
      )

   private val config = configKeeper.config

   //add a shutdown hook to save config into file
   init { addShutdownHook { configKeeper.save() } }

   private val logger = KotlinLogging.logger {  }

   @JvmStatic fun main(args:Array<String>) = runBlocking<Unit> {
      if (config.startSignal >1){
         config.startSignal--
         logger.warn { "Config dont exist,write default config into forward/discord.json" }
         logger.error { "app will exit,please modify config" }
      } else if (config.startSignal == 1){
         try {
            start()
         }catch (e:Throwable){
            logger.error(e) { "start up failed \n" + e.stackTrace  }
            exit(1)
         }
      } else {
         logger.warn { "app has been prohibited to start" }
      }
   }

   suspend fun start() = launch {
      val forwardClient = WebForwardClient
         .createFully(
            port = config.port,
            host = config.host,
            uri = config.uri,
            appID = config.appID,
            channelID = config.channelID,
            token = config.forwardToken,
            name = config.name
         )
      eventBus.consumer<String>("forward.source"){
         logger.info { "Received:${it.body()}" }
      }
      while (true){
         val line = readLine()!!
         when(line){
            "/exit" -> {
               forwardClient.close()
               shutdown()
            }
            "/pause" -> { forwardClient.stop() }
            "/resume"  -> { forwardClient.resume() }
            else -> {
               logger.info { "Send:$line" }
               eventBus.publish("forward.${config.name}",line)
            }
         }
      }
   }
}