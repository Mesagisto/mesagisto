package io.github.itsusinn.forward.discord.command

import com.jagrosh.jdautilities.command.CommandClientBuilder
import io.github.itsusinn.extension.jda.command.command
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

fun CommandClientBuilder.addPingCommand():CommandClientBuilder{
   command(
      name = "ping",
      help = "this is ping command"
   ) {
      reply("Pong!,my ping is ${jda.gatewayPing}")
   }
   return this
}
fun CommandClientBuilder.addBindCommand():CommandClientBuilder{
   command(
      name = "bind",
      help = "this is bind command"
   ) {
      val argus = args.split("").iterator()
      reply("argus:$argus")
   }
   return this
}
