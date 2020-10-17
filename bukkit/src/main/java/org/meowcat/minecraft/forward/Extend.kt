package org.meowcat.minecraft.forward
/*
 Some of code is from https://github.com/Shynixn/MCCoroutine
 */
import com.github.shynixn.mccoroutine.launchAsync
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

val plugin
    get() = JavaPlugin.getPlugin(Forward::class.java)

fun launch(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(Forward::class.java).launch(f)
}
fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit){
    JavaPlugin.getPlugin(Forward::class.java).launch(dispatcher,f)
}
fun launchAsync(f: suspend CoroutineScope.() -> Unit) {
    JavaPlugin.getPlugin(Forward::class.java).launchAsync(f)
}
val Dispatchers.Minecraft: CoroutineContext
    get() {
        return JavaPlugin.getPlugin(Forward::class.java).minecraftDispatcher
    }

val Dispatchers.Async: CoroutineContext
    get() {
        return JavaPlugin.getPlugin(Forward::class.java).asyncDispatcher
    }

val logger
    get() = Bukkit.getLogger()

val allBots
    get() = Forward.allBots

val defaultConfig = """
    agentList:
    - account: "123456789"
      password: "123456789"
    target: 123456789
""".trimIndent()


