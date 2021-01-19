package io.github.itsusinn.forward.dispatcher.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PathArgu(
   @JsonProperty(value = "app_id")
   val appID:String,
   @JsonProperty(value = "channel_id")
   val channelID:String,
   @JsonProperty(value = "token")
   var token:String
)