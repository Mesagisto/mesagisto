package org.meowcat.minecraft.forward

import net.mamoe.mirai.Bot
import net.mamoe.mirai.containsGroup
import org.kodein.di.DI
import org.kodein.di.instance
import org.meowcat.minecraft.forward.service.ConfigService

/**
 * 一个bot调度器
 * 决定哪个监听监听
 * 哪些bot进行发送
 * 还负责校检合法性
 */
class BotDispatcher(di:DI){
   private val configService by di.instance<ConfigService>()

   private val config by lazy{ configService.config }
   val creators = config.creators
   val allBots = HashSet<Bot>()
   val speakers = HashSet<Bot>()
   var listener = 12345678L
   var target = config.target

   fun reDispatch(){
      changeTarget(this.target)
   }

   fun addBot(bot: Bot):BotDispatcher{
      allBots.add(bot)
      reDispatch()
      return this
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