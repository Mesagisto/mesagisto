package io.github.itsusinn.forward.discord

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.itsusinn.extension.jackson.writeValueAsString

data class ConfigData(
   /**
    * a signal value
    * when autoStart > 1 autoStart disable and autoStart--
    * when autoStart = 1 autoStart enable
    * when autoStart < 1 autoStart disable
    */
   @JsonProperty(value = "auto_start")
   var autoStart:Int = 2,

   @JsonProperty(value = "forward_token")
   var forwardToken:String,
   @JsonProperty(value = "discord_token")
   var discordToken:String,

   var host:String,
   var port:Int,
   var uri:String,

   @JsonProperty(value = "app_id")
   var appID:String,
   @JsonProperty(value = "channel_id")
   var channelID:String
)
val defaultConfig:String
   get() = ConfigData(
      2,
      "null",
      "null",
      "null",
      1431,
      "null",
      "test_app_id",
      "test_channel_id"
   ).writeValueAsString()