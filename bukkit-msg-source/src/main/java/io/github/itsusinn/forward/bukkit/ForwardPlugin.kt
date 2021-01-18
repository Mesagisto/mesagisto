package io.github.itsusinn.forward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.bukkit.plugin.java.JavaPlugin

private val logger = KotlinLogging.logger {  }

class ForwardPlugin : JavaPlugin() {


   override fun onLoad() {
      server.getPluginCommand("forward")!!.setSuspendingExecutor(CommandExecutor)
      server.pluginManager.registerSuspendingEvents(AsyncPlayerChatEventListener,this)
   }

   override fun onEnable(){}

   override fun onDisable() {}
}