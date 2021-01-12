package io.github.itsusinn.forward.dispatcher.repo

import io.github.itsusinn.forward.dispatcher.data.PathArgu
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

fun checkToken(pathArgu: PathArgu):Boolean{
   try {
      return doCheckToken(pathArgu)
   } catch (e:Exception){
      return false
   }
}

fun doCheckToken(pathArgu: PathArgu):Boolean{
   //do some thing without caring exception
   logger.warn{
      """
      Connecting 
      AppId:${pathArgu.appID}
      ChannelID:${pathArgu.channelID}
      """.trimIndent()
   }
   return true
}
private object Logger