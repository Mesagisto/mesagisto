package io.github.itsusinn.forward.dispatcher.repo

import io.github.itsusinn.forward.dispatcher.data.PathArgu
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

fun checkToken(appID:String,channelID:String,token:String):Boolean{
   logger.warn { "New ws connect connecting " }
   logger.warn { "AppId:${appID}" }
   logger.warn { "ChannelID:${channelID}" }
   return true
}
fun checkToken(address:String,token:String):Boolean{
   val para = address.split(".")
   if (para.size!=2) return false
   logger.warn { "New ws connect connecting " }
   logger.warn { "AppId:${para[0]}" }
   logger.warn { "ChannelID:${para[1]}" }
   return true
}
