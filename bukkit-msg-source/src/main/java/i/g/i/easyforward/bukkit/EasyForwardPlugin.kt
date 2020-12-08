package i.g.i.easyforward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import i.g.i.easyforward.bukkit.extension.KotlinPlugin
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpClient
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

class EasyForwardPlugin : KotlinPlugin() {
   private val vertx = Vertx.vertx()
   private val di = DI{
      bind<EventBus>() with singleton { vertx.eventBus() }
      bind<HttpClient>() with singleton { vertx.createHttpClient() }
      bind<MessageListener>() with singleton { MessageListener(di) }
      bind<MessageSpeaker>() with singleton { MessageSpeaker(di) }
      bind<SuspendingCommandExecutor>() with singleton { EasyForwardCommandExecutor(di) }
   }
   override fun onEnable() = runBlocking {
      val easyForward = EasyForward(di)
      easyForward.onEnable()

      server.getPluginCommand("forward")!!.setSuspendingExecutor(easyForward.easyForwardCommandExecutor)
      server.pluginManager.registerSuspendingEvents(easyForward.messageListener, this@EasyForwardPlugin)
   }
}