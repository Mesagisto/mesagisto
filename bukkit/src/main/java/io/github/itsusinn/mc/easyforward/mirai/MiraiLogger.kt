package io.github.itsusinn.mc.easyforward.mirai

import net.mamoe.mirai.utils.MiraiLoggerPlatformBase
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.logging.Level
import java.util.logging.Logger

class MiraiLogger(di:DI,override val identity: String?) : MiraiLoggerPlatformBase(){
   private val logger:Logger by di.instance()
   override fun debug0(message: String?, e: Throwable?) {
      //logger.log(Level.WARNING,identity+message,e)
   }

   override fun error0(message: String?, e: Throwable?) {
      logger.log(Level.SEVERE, identity + message, e)
   }

   override fun info0(message: String?, e: Throwable?) {
      //logger.log(Level.INFO,identity+message,e)
   }

   override fun verbose0(message: String?, e: Throwable?) {
      //logger.log(Level.INFO,identity+message,e)
   }

   override fun warning0(message: String?, e: Throwable?) {
      logger.log(Level.WARNING,identity+message,e)
   }

}