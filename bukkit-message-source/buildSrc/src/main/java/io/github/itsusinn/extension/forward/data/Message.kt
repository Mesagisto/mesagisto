package io.github.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonProperty

abstract class Message: Pack

data class TextMessage(
   @JsonProperty(value = "sender_id")
   val senderID:String,
   val content:String,
):Message()

fun textMessage(senderID: String,content: String):FrameData{
   return FrameData(200,TextMessage(senderID, content))
}
