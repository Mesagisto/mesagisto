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
