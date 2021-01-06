package io.github.itsusinn.extension.config

import io.github.itsusinn.extension.jackson.readValue
import org.apache.logging.log4j.core.net.Facility
import java.io.File

class ConfigKeeper<T> (
   val config:T
){

   companion object Factory{
      inline fun <reified T> create(defaultConfig:String,file:File):ConfigKeeper<T>?{
         val config = readConfigFromFile<T>(defaultConfig, file)
         return ConfigKeeper<T>(config)
      }
   }
}

inline fun <reified T> readConfigFromFile(defaultConfig:String,file:File):T{
   if (!file.exists()){
      file.createNewFile()
      file.writeText(defaultConfig)
   }
   return readValue<T>(file.readBytes()) ?: readValue<T>(defaultConfig)!!
}