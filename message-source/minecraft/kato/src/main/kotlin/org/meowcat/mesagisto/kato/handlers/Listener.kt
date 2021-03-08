package org.meowcat.mesagisto.kato.handlers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.meowcat.mesagisto.kato.Plugin
import org.meowcat.mesagisto.kato.Plugin.CONFIG
import kotlin.coroutines.CoroutineContext

object Listener : Listener, CoroutineScope {

  private fun handlePlayerChat(event: AsyncPlayerChatEvent) {
    if (!CONFIG.switch.chat) return
    launch {
      send(event)
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  fun handlePlayerJoin(event: PlayerJoinEvent) {
    if (!CONFIG.switch.join) return
    launch {
      sendPlayerJoin(event)
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  fun handlePlayerLeave(event: PlayerQuitEvent) {
    if (!CONFIG.switch.leave) return
    launch {
      sendPlayerLeave(event)
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  fun handlePlayerDeath(event: PlayerDeathEvent) {
    if (!CONFIG.switch.death) return
    launch {
      sendPlayerDeath(event)
    }
  }

  // disgusting and stupid event dispatch system
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  fun handleLowest(event: AsyncPlayerChatEvent) {
    if (CONFIG.eventPriority != EventPriority.LOWEST) return
    handlePlayerChat(event)
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  fun handleLow(event: AsyncPlayerChatEvent) {
    if (CONFIG.eventPriority != EventPriority.LOW) return
    handlePlayerChat(event)
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  fun handleNormal(event: AsyncPlayerChatEvent) {
    if (CONFIG.eventPriority != EventPriority.NORMAL) return
    handlePlayerChat(event)
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  fun handleHigh(event: AsyncPlayerChatEvent) {
    if (CONFIG.eventPriority != EventPriority.HIGH) return
    handlePlayerChat(event)
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  fun handleHighest(event: AsyncPlayerChatEvent) {
    if (CONFIG.eventPriority != EventPriority.HIGHEST) return
    handlePlayerChat(event)
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  fun handleMonitor(event: AsyncPlayerChatEvent) {
    if (CONFIG.eventPriority != EventPriority.MONITOR) return
    handlePlayerChat(event)
  }
  override val coroutineContext: CoroutineContext
    get() = Plugin.coroutineContext
}
