package io.github.itsusinn.mc.easyforward.service

import net.mamoe.mirai.Bot
import net.mamoe.mirai.containsGroup
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.logging.Logger

/**
 * 一个bot调度器
 * 决定哪个监听监听
 * 哪些bot进行发送
 * 还负责校检合法性
 */
class BotDispatcher(di:DI) {
   private val configService by di.instance<ConfigService>()
   private val config by lazy { configService.config }
   private val logger by di.instance<Logger>()

   val creators = config.creators
   val allBots = HashSet<Bot>()
   val speakers = HashSet<Bot>()
   private var listener = 12345678L
   private var target = config.target

   private fun reDispatch() {
      changeTarget(this.target)
   }

   fun getListener(): Long = listener

   fun getTarget(): Long = target

   fun randomSpeaker(): Bot {
      var r = speakers.random()
      while (!r.isOnline) {
         logger.warning("${r.nick} is not online,it will be removed")
         removeBot(r)
         r = speakers.random()
      }
      return r
   }

   fun addBot(bot: Bot) {
      allBots.add(bot)
      logger.info("${bot.id} Added to AllBots")
      reDispatch()
   }

   fun removeBot(bot: Bot){
      allBots.remove(bot)
      logger.warning("${bot.id} Removed from AllBots")
      reDispatch()
   }

   fun changeTarget(target:Long){
      this.target = target
      if (allBots.isEmpty()) return
      //清除speaker
      speakers.clear()
      allBots.forEach {
         it.containsGroup(target)
         speakers.add(it)
      }
      listener = speakers.random().id
      configService.config.target = target
   }

   fun findBotByID(id:Long):Bot?{
      allBots.forEach {
         if (it.id == id) return it
      }
      return null
   }
}