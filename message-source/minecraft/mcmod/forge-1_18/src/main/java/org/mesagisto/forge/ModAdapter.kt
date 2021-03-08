package org.mesagisto.forge

import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.mesagisto.forge.impl.ChatImpl
import org.mesagisto.mcmod.ModEntry

val logger: Logger = LogManager.getLogger("mesagisto")

@net.minecraftforge.fml.common.Mod("mesagisto")
class ModAdapter {
  private lateinit var server: MinecraftServer
  init {
    MinecraftForge.EVENT_BUS.addListener(ChatImpl::deliverChatEvent)
    MinecraftForge.EVENT_BUS.addListener(this::onServerStart)
    MinecraftForge.EVENT_BUS.addListener(this::onServerStop)
  }
  private fun onServerStart(
    event: ServerStartingEvent
  ) {
    server = event.server
    ChatImpl.server = event.server
    ModEntry.onEnable()
  }
  private fun onServerStop(
    event: ServerStoppingEvent
  ) {
    ModEntry.onDisable()
  }
}
