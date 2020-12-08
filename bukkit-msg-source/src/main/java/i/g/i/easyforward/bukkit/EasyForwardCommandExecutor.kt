package i.g.i.easyforward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import io.vertx.core.eventbus.EventBus
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.kodein.di.DI
import org.kodein.di.instance

class EasyForwardCommandExecutor(di:DI): SuspendingCommandExecutor {
   private val eventBus by di.instance<EventBus>()

   override suspend fun onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array<out String>
   ): Boolean {
      return if (doCommand(sender,args) ) {
         true
      }else{
         sender.sendMessage("")
         //Help Message
         false
      }
   }

   private suspend fun doCommand(
      sender: CommandSender,
      args: Array<out String>
   ):Boolean{
      val size = args.size-1
      if (args.isEmpty()) return false
      when("${args[0].toLowerCase()}:${args.size-1}"){
         "setserver:1"-> {
            args[1]
         }
         "enable:0" -> {
            eventBus.publish(Address.ConfigurationChange,Operation.Enable)
         }
         else -> {
            return false
         }
      }
      return false
   }
}