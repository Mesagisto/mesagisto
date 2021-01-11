package io.github.itsusinn.forward.discord

import io.github.itsusinn.extension.config.ConfigKeeper
import io.github.itsusinn.extension.forward.WebForwardClient
import io.github.itsusinn.extension.jda.DiscordBotClient
import io.github.itsusinn.extension.runtime.addShutdownHook
import io.github.itsusinn.extension.runtime.exit
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.extension.vertx.eventloop.eventBus
import io.vertx.core.Vertx
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.io.File

object App : SingleThreadCoroutineScope("forward") {
   //Make sure the forward folder exists
   init { File("forward").apply { mkdir() } }

   val configKeeper = ConfigKeeper
      .create<DiscordConfigData>(
         defaultConfig, File("forward/discord.json")
      )
   //add a shutdown hook to save config into file
   init { addShutdownHook { configKeeper.save() } }

   val config = configKeeper.config
   private val logger = KotlinLogging.logger(javaClass.name)

   /**
    * about start signal,
    * see [DiscordConfigData]
    */
   @JvmStatic fun main(args:Array<String>){

      if (config.startSignal >1){
         config.startSignal--
         logger.warn { "Config dont exist,write default config into forward/discord.json" }
         logger.error { "app will exit,please modify config" }
      } else if (config.startSignal == 1){
         launch {
            try {
               start()
            }catch (e:Throwable){
               logger.error(e) { "start up failed \n" + e.stackTrace  }
               exit(1)
            }
         }
      } else {
         logger.warn { "app has been prohibited to start" }
      }
      configKeeper.save()
   }

   suspend fun start() {
      val discordClient = DiscordBotClient
         .create(
            token = config.discordToken
         )
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
      //test
      eventBus.consumer<String>("forward.source"){
         logger.info { "Received:${it.body()}" }
      }
   }
}

