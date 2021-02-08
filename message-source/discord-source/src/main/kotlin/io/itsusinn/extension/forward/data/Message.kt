package io.itsusinn.extension.forward.data

import com.fasterxml.jackson.annotation.JsonTypeInfo

abstract class Message: Frame

data class TextMessage(
   val senderID:Long,
   val content:String,
   ):Message()
