package io.github.itsusinn.forward.discord

import io.github.itsusinn.extension.config.ConfigKeeper
import io.github.itsusinn.extension.forward.WebForwardClient
import io.github.itsusinn.extension.log.staticInlineLogger
import io.github.itsusinn.extension.runtime.addShutdownHook
import io.github.itsusinn.extension.runtime.exit
import io.github.itsusinn.extension.thread.SingleThreadLoop
import io.github.itsusinn.extension.vertx.eventloop.eventBus
import kotlinx.coroutines.runBlocking
import java.io.File

object App : SingleThreadLoop() {
   val forwardClient = WebForwardClient.create()

   private val dir = File("forward").apply { mkdir() }

   val configKeeper = ConfigKeeper.create<DiscordConfigData>(
      defaultConfig,
      File("forward/discord.json")
   )
   val config = configKeeper.config
   val logger = staticInlineLogger()

   init {
      addShutdownHook { configKeeper.save() }
   }

   @JvmStatic fun main(args:Array<String>){

      if (config.startSignal >1){
         config.startSignal--

         logger.warn { "Config dont exist,write default config into forward/discord.json" }
         logger.error { "app will exit,please modify config" }
      } else if (config.startSignal == 1){
         start()
      } else {
         logger.warn { "app has been prohibited to start" }
      }
      configKeeper.save()
   }

   fun start() = runBlocking<Unit>{
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
      try {
         forwardClient.link()
         //test
         eventBus.consumer<String>("forward.source"){
            logger.info { "Received:${it.body()}" }
         }
      }catch (e:Throwable){
         logger.error { "please modify config" }
         exit(1)
      }
   }
}

