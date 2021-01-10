package io.github.itsusinn.extension.jda

import io.github.itsusinn.extension.log.logger
import io.github.itsusinn.extension.thread.SingleThreadLoop
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.ConcurrentHashMap

object Listener: EventListener, SingleThreadLoop(DiscordBotClient) {

   private val handlers = ConcurrentHashMap<String,EventHandler>()
   override fun onEvent(event: GenericEvent) {
      handlers.forEach { doHandleEvent(event,it.value) }
   }

   private fun doHandleEvent(event: GenericEvent, handler:EventHandler){
      launch {
         try {
            handler(event)
         }catch (e:Throwable){
            logger.error(e){ e.printStackTrace() }
         }
      }
   }

   fun register(name:String, handler:EventHandler) = handlers.put(name,handler)
   fun unregister(name: String) = handlers.remove(name)
}

typealias EventHandler = suspend (GenericEvent) -> Unit

inline fun <reified T> listenEvent(
   name:String,
   noinline handler:EventHandler
) where T: GenericEvent {
   Listener.register(name){
      if (it is T){ handler(it) }
   }
}
inline fun <reified T> listenEventOnce(
   name:String,
   noinline handler:EventHandler
) where T: GenericEvent {
   Listener.register(name){
      if (it is T){
         handler(it)
         Listener.unregister(name)
      }
   }
}