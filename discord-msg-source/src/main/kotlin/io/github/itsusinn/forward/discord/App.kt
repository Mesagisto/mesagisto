package io.github.itsusinn.forward.discord

import io.github.itsusinn.extension.config.ConfigKeeper
import io.github.itsusinn.extension.console.Console
import java.io.File

object App {
   val configKeeper =
      ConfigKeeper.create<ConfigData>(
         defaultConfig,
         File("config.json")
      )
   @JvmStatic fun main(args:Array<String>){

      Console.startListen()
      Console.registerHandlers()
   }
}

fun Console.registerHandlers(){
   registerHandler("start"){

      return@registerHandler null
   }
   registerHandler("set"){
      if (!it.hasNext()) return@registerHandler null
      when(it.next()){
         "appID" -> {
         }
         "channelID" -> {

         }
         "forwardToken" -> {

         }
         "discordToken" -> {

         }
      }
      return@registerHandler null
   }

}