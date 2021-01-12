package io.github.itsusinn.extension.jda.command

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent

typealias JdaCommand = com.jagrosh.jdautilities.command.Command

fun CommandClientBuilder.command(
   name:String = "null",
   help:String = "no help available",
   cooldown:Int = 0,
   handler:CommandEvent.() -> Unit
){
   addCommand(Command(name, help, cooldown, handler))
}

fun Command(
   name:String = "null",
   help:String = "no help available",
   cooldown:Int = 0,
   handler:CommandEvent.() -> Unit
): JdaCommand {
   return object : JdaCommand(){
      override fun execute(event: CommandEvent) {
         handler.invoke(event)
      }
      init {
         this.name = name
         this.help = help
         this.cooldown = cooldown
      }
   }
}

class PingCommand: JdaCommand() {
   init {

   }
   override fun execute(event: CommandEvent?) {
      TODO("Not yet implemented")
   }
}

