package io.github.itsusinn.extension.jda

import net.dv8tion.jda.api.JDABuilder

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object JdaManager {
   fun init(){
      val jda = JDABuilder
         .createDefault(token)
         .addEventListeners(MessageListener)
         .build()
      jda.awaitReady()
   }
}
private object MessageListener:ListenerAdapter(){
   override fun onMessageReceived(event: MessageReceivedEvent){

   }
}