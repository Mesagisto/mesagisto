package io.itsusinn.forward.extension.config

import io.itsusinn.extension.jackson.asString
import io.itsusinn.extension.jackson.readValue
import java.io.File

class ConfigKeeper<T> (
   val config: T,
   private val file: File
) {
   fun save() {
      file.writeText(config.asString ?: "error when writing config")
   }
   companion object Factory {
      inline fun <reified T> create(defaultConfig: String, file: File): ConfigKeeper<T> {
         val config = readConfigFromFile<T>(defaultConfig, file)
         return ConfigKeeper<T>(config, file)
      }
   }
}

inline fun <reified T> readConfigFromFile(defaultConfig: String, file: File): T {
   if (!file.exists()) {
      file.createNewFile()
      file.writeText(defaultConfig)
   }
   return readValue<T>(file.readBytes()) ?: run {
      file.writeText(defaultConfig)
      readValue<T>(defaultConfig)!!
   }
}
