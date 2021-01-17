package io.github.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo

abstract class Message: Pack

data class TextMessage(
   @JsonProperty(value = "sender_id")
   val senderID:Long,
   val content:String,
):Message()

fun textMessage(senderID: Long,content: String):FrameData{
   return FrameData(200,TextMessage(senderID, content))
}
