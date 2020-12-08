package i.g.i.easyforward.bukkit.data

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
sealed class Message{
   class TextMessage(val content:String): Message()
   class ImageMessage(val url:String): Message()
}

data class MessageFrame(
   val sender: String,
   val chain:List<Message>
   )
fun MessageFrame(sender: String,msg:Message): MessageFrame {
   return MessageFrame(sender, listOf(msg))
}
fun MessageFrame(sender: String,msg:String): MessageFrame {
   return MessageFrame(sender, listOf(Message.TextMessage(msg)))
}