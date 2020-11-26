package org.meowcat.minecraft.forward.service

import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import org.meowcat.minecraft.forward.BotDispatcher
import org.meowcat.minecraft.forward.data.Config
import org.meowcat.minecraft.forward.decodeFromString
import org.meowcat.minecraft.forward.encodeToString
import org.meowcat.minecraft.forward.extension.defaultConfig
import java.io.File
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

/*
    当一个ConfigSolver被创建时就应该确保配置文件已经存在
 */
class ConfigService(di: DI):CoroutineScope{

   override val coroutineContext:CoroutineContext by di.instance("async")
   val minecraftDispatcher:CoroutineContext by di.instance("minecraft")

   private val botLoginService: BotLoginService by di.instance()
   private val botDispatcher:BotDispatcher by di.instance()

   private val logger:Logger by di.instance()

   //对象化的配置
   val config: Config
   //配置文件
   private val file = File("forward.yml")
   //文件的内容
   private var content = ""

   init {
      //如果没有配置文件则新建一个,并写入默认配置
      if (!file.exists()){
         file.createNewFile()
         file.writeText(defaultConfig)
         logger.info("不存在配置文件->写入默认配置")

      }
      content = file.readText()
      config = decodeFromString(Config.serializer(), content)
   }

   /**
    * 保存配置文件
    */
   fun save() = GlobalScope.launch {

      try {
         withContext(Dispatchers.Default){
            //序列化
            content = encodeToString(Config.serializer(), config)
         }
         withContext(Dispatchers.IO){
            //写入文件
            file.writeText(content)
         }
      }catch (e:Exception){
         e.printStackTrace()
      }
   }

   /**
    * 实现从配置中加载保存的bot对象的登陆操作
    * 登陆交由 BotLoginSolver 实现
    */
   fun load(){
      logger.info("从配置中准备加载${config.botList.size-1}个Bot")
      config.botList.forEach {
         botLoginService.autoLogin(it)
      }
      botDispatcher.changeTarget(config.target)
   }


}