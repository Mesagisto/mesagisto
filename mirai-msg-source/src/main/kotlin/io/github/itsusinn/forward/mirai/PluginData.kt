package io.github.itsusinn.forward.mirai

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object ForwardConfig : AutoSavePluginConfig("forward") {

   var startSignal:Int by value(1)
   var target:Long by value(123456L)
   val host:String by value("127.0.0.1")
   val port:Int by value(1431)
   val uri:String by value("/ws")
   val appID:String by value("test_app_id")
   val channelID:String by value("test_channel_id")
   val token:String by value("test_token")

//   val value1 by value<Int>() // 推断为 Int
//   val value2 by value(0) // 默认值为 0， 推断为 Int
//   var value3 by value(0) // 支持 var，修改会自动保存
//   val value4: Int by value() // 显式类型和推断类型，你喜欢哪种？
//   val value5: List<String> by value() // 支持 List，Set
//   val value6: MutableList<String> by value() // 可按需使用 Mutable 类型
//   val value7: List<List<String>> by value() // 支持嵌套
//   val value8: Map<String, List<List<String>>> by value() // 支持 Map
//
//   var value9: List<String> by value() // List、Set 或 Map 同样支持 var。但请注意这是非引用赋值（详见下文）。
}