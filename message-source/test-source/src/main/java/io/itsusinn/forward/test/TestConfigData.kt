/**
 * Copyright 2020-2021 Itsusinn and contributors.
 *
 * Licensed under the GNU Lesser General Public License version 3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://opensource.org/licenses/LGPL-3.0
 */

package itsusinn.forward.test

import com.fasterxml.jackson.annotation.JsonProperty
import io.itsusinn.extension.jackson.asPrettyString

data class TestConfigData(
   /**
    * a signal value
    * when [startSignal] > 1 forward will exit directly and [startSignal] --
    * when [startSignal] = 1 forward will start
    * when [startSignal] < 1 forward will exit directly
    */
   @JsonProperty(value = "start_signal")
   var startSignal: Int = 2,

   @JsonProperty(value = "forward_token")
   val forwardToken: String,

   val host: String,
   val port: Int,

   val address: String,
   val name: String,
)

val defaultConfig: String by lazy {
   TestConfigData(
      2,
      "test_token",
      "127.0.0.1",
      1431,
      "test_app_id.test_channel_id",
      "test_name",
   ).asPrettyString!!
}
