package org.meowcat.mesagisto.kato.platform

import org.bukkit.plugin.java.JavaPlugin
import org.meowcat.mesagisto.kato.Plugin

class Bukkit : JavaPlugin() {

  private val inner: JvmPlugin = Plugin
  override fun onLoad() {
    inner.onLoad(this@Bukkit).getOrThrow()
  }
  override fun onEnable() {
    inner.onEnable().getOrThrow()
  }

  override fun onDisable() {
    inner.onDisable().getOrThrow()
  }
}
