package i.g.i.easyforward.bukkit.service

import i.g.i.easyforward.bukkit.data.Config
import i.g.i.easyforward.bukkit.data.defaultConfig
import i.g.i.easyforward.bukkit.extension.decodeFromString
import i.g.i.easyforward.bukkit.extension.encodeToString
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
   private val storageService: StorageService by di.instance()
   private val logger:Logger by di.instance()
   val config: Config = storageService.config

   fun save() = storageService.flush()

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