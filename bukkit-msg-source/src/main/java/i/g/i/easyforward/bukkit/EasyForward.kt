package i.g.i.easyforward.bukkit

import org.kodein.di.DI
import org.kodein.di.instance

class EasyForward(private val di:DI) {
   val messageSpeaker:MessageSpeaker by di.instance()
   val messageListener:MessageListener by di.instance()
   private lateinit var easyForwardClient:EasyForwardClient
   suspend fun create(){
      easyForwardClient = EasyForwardClient("127.0.0.1",1431,di)
      easyForwardClient.onCreate()
   }

}