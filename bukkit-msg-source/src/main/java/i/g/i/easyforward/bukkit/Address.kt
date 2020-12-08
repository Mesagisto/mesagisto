package i.g.i.easyforward.bukkit

object Address {
   const val Listen = "listen"
   const val Speak = "speak"
   const val Send = "send"
   const val Receive = "receive"
   const val SpeakToOp = "speak.op"
   const val Configuration = "configuration"
   const val ConfigurationChange = "configuration.change"
}
sealed class Operation{
   object Enable
   class SetServer(val address:String)
}