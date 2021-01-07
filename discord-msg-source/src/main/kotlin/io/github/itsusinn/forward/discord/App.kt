package io.github.itsusinn.forward.discord

import io.github.itsusinn.extension.config.ConfigKeeper
import io.github.itsusinn.extension.console.Console
import io.github.itsusinn.extension.forward.ForwardClient
import io.github.itsusinn.extension.thread.SingleThread
import java.io.File

object App : SingleThread() {
   val forwardClient = ForwardClient.create()

   val configKeeper = ConfigKeeper.create<ConfigData>(
      defaultConfig,
      File("config.json")
   )
   val config = configKeeper.config
   @JvmStatic fun main(args:Array<String>){
      Console.startListen()
      Console.registerHandlers()
   }
}

fun Console.registerHandlers(){

   handle(
      "start",
      info = "start server"
   ){
      //start forward client
      //start discord listen
      return@handle null
   }

   handle(
      "set",
      info = "set a series of arguments"
   ){
      if (!hasNext()) return@handle null
      val second = next()
      if (!hasNext()) return@handle null
      when(second){
         "appID" -> {
            App.config.appID = next()
            return@handle "set appID successfully"
         }
         "channelID" -> {
            App.config.channelID = next()
            return@handle "set channelID successfully"
         }
         "forwardToken" -> {
            App.config.forwardToken = next()
            return@handle "set forwardToken successfully"
         }
         "discordToken" -> {
            App.config.discordToken = next()
            return@handle "set discordToken successfully"
         }
      }
      return@handle null
   }

}