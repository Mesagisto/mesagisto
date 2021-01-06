package io.github.itsusinn.forward.discord

import io.github.itsusinn.extension.jackson.writeValueAsString
import java.io.File

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