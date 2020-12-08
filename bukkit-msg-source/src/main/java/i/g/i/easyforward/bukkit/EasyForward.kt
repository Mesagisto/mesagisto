package i.g.i.easyforward.bukkit

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import org.kodein.di.DI
import org.kodein.di.instance

class EasyForward(private val di:DI) {
   private val messageSpeaker:MessageSpeaker by di.instance()
   val messageListener:MessageListener by di.instance()
   val easyForwardCommandExecutor:SuspendingCommandExecutor by di.instance()

   private lateinit var easyForwardClient:EasyForwardClient

   suspend fun onEnable(){

      easyForwardClient = EasyForwardClient("127.0.0.1",1431,di)
      easyForwardClient.onEnable()
      messageSpeaker.onEnable()

   }

}