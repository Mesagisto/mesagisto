package i.g.i.easyforward.bukkit

import i.g.i.easyforward.bukkit.extension.publish
import io.vertx.core.eventbus.EventBus
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit


import org.kodein.di.DI
import org.kodein.di.instance

class MessageSpeaker(di:DI) {
   private val eventBus: EventBus by di.instance()
   fun onEnable(){
      eventBus.consumer<BaseComponent>(Address.Speak){
         publish(it.body())
      }
      eventBus.consumer<String>(Address.SpeakToOp){
         Bukkit.broadcast(it.body(),"forward.use")
      }
   }
   fun onDisable(){
      //TODO
   }
}