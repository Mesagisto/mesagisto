package io.github.itsusinn.forward.dispatcher.repo

import io.github.itsusinn.extension.logger
import io.github.itsusinn.forward.dispatcher.data.PathArgu

fun checkToken(pathArgu: PathArgu):Boolean{
   try {
      return doCheckToken(pathArgu)
   } catch (e:Exception){
      return false
   }
}

fun doCheckToken(pathArgu: PathArgu):Boolean{
   //do some thing without caring exception
   Logger.logger.warn(
      """
      Connecting 
      AppId:${pathArgu.appID}
      ChannelID:${pathArgu.channelID}
      """.trimIndent()
   )
   return true
}
private object Logger