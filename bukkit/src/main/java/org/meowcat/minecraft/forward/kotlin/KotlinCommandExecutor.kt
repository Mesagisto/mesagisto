package org.meowcat.minecraft.forward.kotlin

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class KotlinCommandExecutor:SuspendingCommandExecutor {
    override suspend fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        TODO("Not yet implemented")
    }
}