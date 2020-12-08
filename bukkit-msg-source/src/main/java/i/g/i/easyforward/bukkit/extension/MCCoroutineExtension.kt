package i.g.i.easyforward.bukkit.extension

import com.github.shynixn.mccoroutine.asyncDispatcher
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.java.JavaPlugin

open class KotlinPlugin : JavaPlugin(), CoroutineScope {
   override val coroutineContext by lazy{ asyncDispatcher }
}