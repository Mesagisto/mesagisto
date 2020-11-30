package io.github.itsusinn.mc.easyforward.service

import io.github.itsusinn.mc.easyforward.data.Config
import io.github.itsusinn.mc.easyforward.data.defaultConfig
import io.github.itsusinn.mc.easyforward.extension.decodeFromString
import io.github.itsusinn.mc.easyforward.extension.encodeToString
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

/*
    当一个ConfigSolver被创建时就应该确保配置文件已经存在
 */
class ConfigService(di: DI):CoroutineScope{

   override val coroutineContext:CoroutineContext by di.instance("async")

   private val botLoginService: BotLoginService by di.instance()
   private val botDispatcher: BotDispatcher by di.instance()

   private val logger:Logger by di.instance()

   val config: Config
   //配置文件
   private val file = File("forward.yml")
   //文件的内容
   private var content = ""

   init {
      var configInstance: Config
      //如果没有配置文件则新建一个,并写入默认配置
      if (!file.exists()){
         logger.info("不存在配置文件->写入默认配置")
         file.createNewFile()
         file.writeText(defaultConfig)
      }
      content = file.readText()
      try {
         configInstance = decodeFromString(Config.serializer(), content)
      }catch (e:Exception){
         logger.warning("配置文件与当前版本不匹配->写入默认配置")
         logger.warning(e.message)
         content = defaultConfig
         configInstance = decodeFromString(Config.serializer(), content)
      }
      config = configInstance
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
         logger.warning("保存配置文件失败")
         e.printStackTrace()
      }
   }

   /**
    * 实现从配置中加载保存的bot对象的登陆操作
    * 登陆交由 BotLoginSolver 实现
    */
   fun load(){
      logger.info("正在从配置中准备加载${config.botList.size-1}个Bot")
      config.botList.forEach {
         botLoginService.autoLogin(it)
      }
      botDispatcher.changeTarget(config.target)
   }
}