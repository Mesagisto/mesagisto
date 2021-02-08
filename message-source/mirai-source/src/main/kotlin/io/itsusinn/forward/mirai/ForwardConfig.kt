package io.itsusinn.forward.mirai

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object ForwardConfig : AutoSavePluginConfig("forward") {

   var startSignal: Int by value(1)
   var host: String by value("127.0.0.1")
   var port: Int by value(1431)
   // address-token
   val addressTokenRepo: MutableMap<String, String> by value(HashMap())

   val targetAddressMapper: MutableMap<Long, String> by value(HashMap())
}
