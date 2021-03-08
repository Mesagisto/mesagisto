package org.mesagisto.mcproxy.platforms

import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import org.mesagisto.mcproxy.Plugin

typealias BungeePlugin = net.md_5.bungee.api.plugin.Plugin

class BungeeAdapter : BungeePlugin() {

  private val inner = Plugin
  override fun onLoad() {
    inner.onLoad(this@BungeeAdapter)
  }
  override fun onEnable() {
    inner.onEnable()
  }

  override fun onDisable() {
    inner.onDisable()
  }
}
