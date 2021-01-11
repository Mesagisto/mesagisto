package io.github.itsusinn.forward.discord.command

import com.jagrosh.jdautilities.command.CommandClientBuilder
import io.github.itsusinn.extension.jda.command.command
import mu.KotlinLogging


object PingCommand{

   private val logger = KotlinLogging.logger {  }

   fun CommandClientBuilder.addPingCommand():CommandClientBuilder{
      command(
         name = "ping",
         help = "this is ping command"
      ) {

      }
      return this
   }
}
