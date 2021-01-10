package io.github.itsusinn.forward.discord

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.itsusinn.extension.jackson.writeValueAsString

data class DiscordConfigData(
   /**
    * a signal value
    * when [startSignal] > 1 forward will exit directly and [startSignal] --
    * when [startSignal] = 1 forward will start
    * when [startSignal] < 1 forward will exit directly
    */
   @JsonProperty(value = "start_signal")
   var startSignal:Int = 2,

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
   var channelID:String,
   var name:String,

   var proxy:Boolean,
   @JsonProperty(value = "proxy_host")
   var proxyHost:String,
   @JsonProperty(value = "proxy_port")
   var proxyPort:Int,
   @JsonProperty(value = "proxy_username")
   var proxyUsername:String,
   @JsonProperty(value = "proxy_password")
   var proxyPassword:String,
)
val defaultConfig:String by lazy {
   DiscordConfigData(
      2,
      "test_token",
      "null",
      "127.0.0.1",
      1431,
      "/ws",
      "test_app_id",
      "test_channel_id",
      "test_name",
      false,
      "127.0.0.1",
      2340,
      "",
      ""
   ).writeValueAsString()
}