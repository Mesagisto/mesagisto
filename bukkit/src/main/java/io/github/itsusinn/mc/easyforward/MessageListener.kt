package io.github.itsusinn.mc.easyforward

import io.github.itsusinn.mc.easyforward.service.BotDispatcher
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.logging.Logger

class MessageListener(di:DI):Listener{

   private val bd: BotDispatcher by di.instance()
   private val logger:Logger by di.instance()
   @EventHandler
   suspend fun onChat(event: AsyncPlayerChatEvent) {
      //没有speakers时直接返回
      if (bd.speakers.isEmpty()) return
      val msg = event.message
      val senderName = event.player.name
      val speaker = bd.randomSpeaker()
      try {
         logger.info("Speaker is ${speaker.id}")
         speaker.getGroup(bd.getTarget()).sendMessage("<$senderName> $msg")
      }catch (e:Exception){
         bd.removeBot(speaker)
         logger.warning("发送消息失败")
      }
   }
}