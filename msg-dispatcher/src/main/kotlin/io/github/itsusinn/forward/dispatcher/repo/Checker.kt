package io.github.itsusinn.forward.dispatcher.repo

import io.github.itsusinn.forward.dispatcher.data.PathArgu
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }


/**
 * TODO check token's legality
 */
fun checkToken(pathArgu: PathArgu):Boolean{
   try {
      return doCheckToken(pathArgu)
   } catch (e:Exception){
      return false
   }
}

fun doCheckToken(pathArgu: PathArgu):Boolean{
   //do some thing without caring exception
   logger.warn { "New ws connect connecting " }
   logger.warn { "AppId:${pathArgu.appID}" }
   logger.warn { "ChannelID:${pathArgu.channelID}" }
   return true
}
private object Logger