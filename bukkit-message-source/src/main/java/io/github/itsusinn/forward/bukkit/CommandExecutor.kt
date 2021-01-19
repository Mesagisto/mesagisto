package io.github.itsusinn.forward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import io.github.itsusinn.extension.jackson.writeAsPrettyString
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(MessageForwardPlugin::class.java)

internal object CommandExecutor: SuspendingCommandExecutor {

   override suspend fun onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array<out String>
   ): Boolean {
      if (args.size != 1) return true

      when (args[0]){
         "reload" -> {
            plugin.onDisable()
            plugin.onEnable()
         }
      }
      return true
   }

}