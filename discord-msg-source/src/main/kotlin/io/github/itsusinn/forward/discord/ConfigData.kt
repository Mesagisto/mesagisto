package io.github.itsusinn.forward.discord

import com.fasterxml.jackson.annotation.JsonProperty

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