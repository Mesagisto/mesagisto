package io.itsusinn.forward.bukkit.extension

import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.minecraftDispatcher
import io.itsusinn.forward.bukkit.MessageForwardPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

fun launch(f: suspend CoroutineScope.() -> Unit): Job {
   return JavaPlugin.getPlugin(MessageForwardPlugin::class.java).launch(f)
}

fun launchAsync(f: suspend CoroutineScope.() -> Unit): Job {
   return JavaPlugin.getPlugin(MessageForwardPlugin::class.java).launchAsync(f)
}

val Dispatchers.minecraft: CoroutineContext
   get() {
      return JavaPlugin.getPlugin(MessageForwardPlugin::class.java).minecraftDispatcher
   }

val Dispatchers.async: CoroutineContext
   get() {
      return JavaPlugin.getPlugin(MessageForwardPlugin::class.java).asyncDispatcher
   }
