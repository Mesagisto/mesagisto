package io.github.itsusinn.forward

import io.itsusinn.forward.mirai.ForwardPluginMain
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

@ConsoleExperimentalApi
suspend fun main() {

   MiraiConsoleTerminalLoader.startAsDaemon()

   ForwardPluginMain.load()
   ForwardPluginMain.enable()

   MiraiConsole.job.join()
}
