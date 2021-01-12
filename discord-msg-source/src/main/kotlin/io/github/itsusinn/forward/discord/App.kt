package io.github.itsusinn.forward.discord

import com.jagrosh.jdautilities.command.CommandClientBuilder
import io.github.itsusinn.extension.config.ConfigKeeper
import io.github.itsusinn.extension.forward.WebForwardClient
import io.github.itsusinn.extension.jda.DiscordBotClient
import io.github.itsusinn.extension.runtime.addShutdownHook
import io.github.itsusinn.extension.runtime.exit
import io.github.itsusinn.extension.thread.SingleThreadCoroutineScope
import io.github.itsusinn.extension.vertx.eventloop.eventBus
import io.github.itsusinn.forward.discord.command.addPingCommand
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.io.File

object App : SingleThreadCoroutineScope("forward") {
   //Make sure the forward folder exists
   init { File("forward").apply { mkdir() } }

   val configKeeper = ConfigKeeper
      .create<ConfigData>(
         defaultConfig, File("forward/discord.json")
      )

   val config = configKeeper.config
   private val logger = KotlinLogging.logger(javaClass.name)

   /**
    * about start signal,
    * see [ConfigData]
    */
   @JvmStatic fun main(args:Array<String>){

      if (config.startSignal >1) {
         config.startSignal--
         logger.warn { "Config dont exist,write default config into forward/discord.json" }
         logger.warn { "app will exit,please modify config" }
         configKeeper.save()
      } else if (config.startSignal == 1){
         try {
            launch {
               start()
            }
         }catch (e:Throwable){
            logger.error(e) { "start up failed \n" + e.stackTrace  }
            exit(1)
         }
      } else {
         logger.warn { "app has been prohibited to start" }
      }
   }

   suspend fun start() {

      val discordClient = DiscordBotClient.create(
         token = config.discord.token)

      val forwardClient = WebForwardClient.createFully(
         port = config.forward.port,
         host = config.forward.host,
         uri = config.forward.uri,
         appID = config.forward.appID,
         channelID = config.forward.channelID,
         token = config.forward.token,
         name = config.forward.name)

      val commands = CommandClientBuilder()
         .setPrefix("/")
         .setOwnerId("795231031082876939")
         .setHelpWord("help")
         .addPingCommand()
         .build()

      discordClient.addEventListener(commands)

      //test
      eventBus.consumer<String>("forward.source"){
         logger.info { "Received:${it.body()}" }
      }
   }
}