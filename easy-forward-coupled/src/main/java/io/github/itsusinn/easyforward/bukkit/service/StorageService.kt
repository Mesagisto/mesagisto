package io.github.itsusinn.easyforward.bukkit.service

import io.github.itsusinn.easyforward.bukkit.data.Config
import io.github.itsusinn.easyforward.bukkit.data.defaultConfig
import io.github.itsusinn.easyforward.bukkit.extension.decodeFromString
import io.github.itsusinn.easyforward.bukkit.extension.encodeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import java.util.logging.Logger

class StorageService(di:DI) {
   private val logger: Logger by di.instance()
   private val file = File("forward.yml")
   private var content = ""
   val config: Config
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
   fun flush() = GlobalScope.launch{
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

}