package io.github.itsusinn.forward.test

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.itsusinn.extension.jackson.writeValueAsString

data class TestConfigData(
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

   var host:String,
   var port:Int,
   var uri:String,

   @JsonProperty(value = "app_id")
   var appID:String,
   @JsonProperty(value = "channel_id")
   var channelID:String,
   var name:String,
)

val defaultConfig by lazy {
   TestConfigData(
      2,
      "test_token",
      "127.0.0.1",
      1431,
      "/ws",
      "test_app_id",
      "test_channel_id",
      "test_name",
   ).writeValueAsString()
}