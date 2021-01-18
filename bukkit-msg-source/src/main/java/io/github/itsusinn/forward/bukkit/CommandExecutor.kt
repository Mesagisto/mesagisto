package io.github.itsusinn.forward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

internal object CommandExecutor: SuspendingCommandExecutor {

   override suspend fun onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array<out String>
   ): Boolean {
      return true
   }

}