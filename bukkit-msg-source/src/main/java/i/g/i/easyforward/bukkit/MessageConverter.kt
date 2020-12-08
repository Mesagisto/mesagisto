package i.g.i.easyforward.bukkit

import i.g.i.easyforward.bukkit.data.Message
import i.g.i.easyforward.bukkit.data.MessageFrame
import io.vertx.core.eventbus.EventBus
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.kodein.di.DI
import org.kodein.di.instance

class MessageConverter(di:DI) {
   private val eventBus by di.instance<EventBus>()
   fun onEnable() {
      eventBus.consumer<AsyncPlayerChatEvent>(Address.Listen) {
         val event = it.body()
         eventBus.publish(
            Address.Send,
            MessageFrame(event.player.name, event.message)
         )
      }
      eventBus.consumer<MessageFrame>(Address.Receive) {
         val messageFrame = it.body()
         val msg = messageFrame.chain[0]
         if ( msg is Message.TextMessage){
            eventBus.publish(
               Address.Speak,
               TextComponent("<${messageFrame.sender}> ${msg.content}")
            )
         }

      }
   }
}