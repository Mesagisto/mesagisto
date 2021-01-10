package io.github.itsusinn.extension.jda

import io.github.itsusinn.extension.log.staticInlineLogger
import io.github.itsusinn.extension.thread.SingleThreadLoop
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.ConcurrentHashMap

object Listener: EventListener, SingleThreadLoop(DiscordBotClient) {

   val logger = staticInlineLogger()

   private val handlers = ConcurrentHashMap<String,suspend (GenericEvent) -> Unit>()

   override fun onEvent(event: GenericEvent) {
      handlers.forEach { doHandleEvent(event,it.value) }
   }

   private fun doHandleEvent(
      event: GenericEvent,
      handler:suspend (GenericEvent) -> Unit
   ){
      launch {
         try {
            handler(event)
         }catch (e:Throwable){
            logger.error(e){ e.printStackTrace() }
         }
      }
   }

   fun register(
      name:String,
      handler:suspend (GenericEvent) -> Unit
   ) {
      handlers.put(name,handler)
   }

   fun unregister(name: String) = handlers.remove(name)
}

inline fun <reified T> listenEvent(
   name:String,
   noinline handler:suspend (T) -> Unit
) where T: GenericEvent {
   Listener.register(name){
      if (it is T){ handler(it) }
   }
}

/**
 * when handler return true,it will be deleted
 */
inline fun <reified T> listenEventOnce(
   name:String,
   noinline handler:suspend (T) -> Boolean
) where T: GenericEvent {
   Listener.register(name){

      if(!(it is T)) return@register

      if (handler(it)) {
         Listener.unregister(name)
      }
   }
}