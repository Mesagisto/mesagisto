package i.g.i.easyforward.bukkit

import io.vertx.core.eventbus.EventBus
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.kodein.di.DI
import org.kodein.di.instance

class MessageListener(di: DI):Listener{
   private val eventBus: EventBus by di.instance()
   @EventHandler
   fun onChat(event: AsyncPlayerChatEvent) {
      eventBus.publish(Address.Listen,event)
   }
}