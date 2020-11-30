package org.meowcat.minecraft.forward.kotlin

import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Minecart
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

open class KotlinPlugin :JavaPlugin(),CoroutineScope {
   override val coroutineContext by lazy{ asyncDispatcher }
}