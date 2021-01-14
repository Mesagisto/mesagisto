package io.github.itsusinn.forward.mirai

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object PluginMain : KotlinPlugin(
   JvmPluginDescription(
      id = "io.github.itsusinn.forward",
      name = "ForwardPlugin",
      version = "0.1.0"
   )
) {

   override fun onEnable() {
      logger.info { "Plugin enabled" }
   }

   override fun onDisable() {
      logger.info { "Plugin disabled"}
   }
}