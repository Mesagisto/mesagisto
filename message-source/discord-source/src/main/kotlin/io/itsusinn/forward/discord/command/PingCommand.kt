// package io.github.itsusinn.forward.discord.command
//
// import com.jagrosh.jdautilities.command.CommandClientBuilder
// import io.github.itsusinn.extension.jackson.asString
// import io.github.itsusinn.extension.jda.command.command
// import io.github.itsusinn.forward.discord.App
// import mu.KotlinLogging
//
// private val logger = KotlinLogging.logger {  }
//
// fun CommandClientBuilder.addPingCommand():CommandClientBuilder{
//   command(
//      name = "ping",
//      help = "this is ping command"
//   ) {
//      reply("Pong!,my ping is ${jda.gatewayPing}")
//   }
//   return this
// }
// fun CommandClientBuilder.addBindCommand():CommandClientBuilder{
//   command(
//      name = "bind",
//      help = "this is bind command"
//   ) {
//      val argus = args.split(" ").iterator()
//      reply("argus:${argus.asString}")
//   }
//   command(
//      name = "show"
//   ) {
//      reply("""
//         discordChannelID: ${channel.id}
//         forward: ${App.config.forward.asString}
//      """.trimIndent())
//   }
//   return this
// }
