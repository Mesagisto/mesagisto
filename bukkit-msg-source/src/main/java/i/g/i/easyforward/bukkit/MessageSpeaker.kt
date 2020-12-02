package i.g.i.easyforward.bukkit

import io.vertx.core.eventbus.EventBus
import org.bukkit.Bukkit
import org.kodein.di.DI
import org.kodein.di.instance

class MessageSpeaker(di:DI) {
   private val eventBus: EventBus by di.instance()
   init {
      eventBus.consumer<String>("in"){
         Bukkit.broadcastMessage(it.body())
      }
   }
}