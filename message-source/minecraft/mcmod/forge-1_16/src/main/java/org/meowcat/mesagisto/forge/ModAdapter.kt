package org.meowcat.mesagisto.forge

import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.server.ServerLifecycleHooks
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.meowcat.mesagisto.forge.impl.ChatImpl
import org.mesagisto.mcmod.ModEntry

val logger: Logger = LogManager.getLogger("mesagisto")

@net.minecraftforge.fml.common.Mod("mesagisto")
class ModAdapter {
  private val server: MinecraftServer by lazy { ServerLifecycleHooks.getCurrentServer() }
  init {
    MinecraftForge.EVENT_BUS.addListener(ChatImpl::deliverChatEvent)
    MinecraftForge.EVENT_BUS.addListener(this::onServerStart)
    MinecraftForge.EVENT_BUS.addListener(this::onServerStop)
  }
  private fun onServerStart(
    event: WorldEvent.Load
  ) {
    server
    ChatImpl.server
    ModEntry.onEnable()
  }

  private fun onServerStop(
    event: WorldEvent.Unload
  ) {
    // ModEntry.onDisable()
  }
}
