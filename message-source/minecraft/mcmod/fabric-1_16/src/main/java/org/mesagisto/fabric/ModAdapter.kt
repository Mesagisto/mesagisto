package org.mesagisto.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.mesagisto.fabric.impl.ChatImpl
import org.mesagisto.mcmod.ModEntry

val logger: Logger = LogManager.getLogger("mesagisto")

class ModAdapter : ModInitializer {
  private lateinit var server: MinecraftServer
  override fun onInitialize() {
    ServerLifecycleEvents.SERVER_STARTED.register {
      server = it
      ChatImpl.server = it
      ModEntry.onEnable()
    }
    ServerLifecycleEvents.SERVER_STOPPING.register {
      ModEntry.onDisable()
    }
  }
}
