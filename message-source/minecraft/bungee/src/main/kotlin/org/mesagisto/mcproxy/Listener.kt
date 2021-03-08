package org.mesagisto.mcproxy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.event.EventHandler
import org.mesagisto.client.Logger
import org.mesagisto.mcproxy.Plugin.CONFIG
import org.mesagisto.mcproxy.handlers.send
import kotlin.coroutines.CoroutineContext

typealias BungeeListener = net.md_5.bungee.api.plugin.Listener

object Listener : BungeeListener, CoroutineScope {
  @EventHandler
  fun handleChat(event: ChatEvent) {
    if (!CONFIG.enable) {
      Logger.error { "Mesagisto信使未被启用！" }
      return
    }
    if (event.isCancelled) return
    launch {
      send(event)
    }
  }

  override val coroutineContext: CoroutineContext
    get() = Plugin.coroutineContext
}
