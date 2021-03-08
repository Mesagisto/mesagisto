/**
 * Copyright 2020-2021 Itsusinn and contributors.
 *
 * Licensed under the GNU Lesser General Public License version 3,
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://opensource.org/licenses/LGPL-3.0
 */

package io.itsusinn.forward.discord

import com.fasterxml.jackson.annotation.JsonProperty
import io.itsusinn.extension.jackson.writeAsString

data class DiscordConfigData(
   val token: String,
   val proxy: Boolean,
   @JsonProperty(value = "proxy_host")
   val proxyHost: String? = null,
   @JsonProperty(value = "proxy_port")
   val proxyPort: Int? = null,
   @JsonProperty(value = "proxy_username")
   val proxyUsername: String? = null,
   @JsonProperty(value = "proxy_password")
   val proxyPassword: String? = null,
)
data class ForwardConfigData(
   val host: String,
   val port: Int,
   val uri: String,
   @JsonProperty(value = "token")
   val token: String,
   @JsonProperty(value = "app_id")
   val appID: String,
   @JsonProperty(value = "channel_id")
   val channelID: String,
   val name: String,
)
data class ConfigData(
   /**
    * a signal value
    * when [startSignal] > 1 forward will exit directly and [startSignal] --
    * when [startSignal] = 1 forward will start
    * when [startSignal] < 1 forward will exit directly
    */
   @JsonProperty(value = "start_signal")
   var startSignal: Int = 2,

   @JsonProperty(value = "discord_config")
   val discord: DiscordConfigData,
   @JsonProperty(value = "forward_config")
   val forward: ForwardConfigData,
)
val defaultConfig: String by lazy {
   ConfigData(
      startSignal = 2,
      discord = DiscordConfigData(
         "null",
         false,
         "127.0.0.1",
         2340,
         "",
         ""
      ),
      forward = ForwardConfigData(
         "127.0.0.1",
         1431,
         "/ws",
         "test_token",
         "test_app_id",
         "test_channel_id",
         "test_name",
      )
   ).writeAsString()!!
}
