package org.meowcat.mesagisto.kato.platform

import org.bukkit.plugin.java.JavaPlugin

abstract class JvmPlugin {
  open fun onEnable(): Result<Unit> = Result.success(Unit)
  open fun onDisable(): Result<Unit> = Result.success(Unit)
  open fun onLoad(bukkit: JavaPlugin): Result<Unit> = Result.success(Unit)
}
