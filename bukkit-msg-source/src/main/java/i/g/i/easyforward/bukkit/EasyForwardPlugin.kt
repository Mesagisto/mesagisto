package i.g.i.easyforward.bukkit

import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

class EasyForwardPlugin : JavaPlugin() {
   private val vertx = Vertx.vertx()
   private val di = DI{
      bind<EventBus>() with singleton { vertx.eventBus() }
      bind<HttpClient>() with singleton { vertx.createHttpClient() }
      bind<MessageListener>() with singleton { MessageListener(di) }
      bind<MessageSpeaker>() with singleton { MessageSpeaker(di) }
   }
   override fun onEnable() {
      EasyForwardClient(di)
      MessageSpeaker(di)
      val listener = MessageListener(di)
      server.pluginManager.registerEvents(listener,this)
   }
}