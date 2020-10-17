package org.meowcat.minecraft.forward.data

import kotlinx.coroutines.*
import org.meowcat.minecraft.forward.*
import org.meowcat.minecraft.forward.mirai.BotLoginSolver
import java.io.File
/*
    当一个ConfigSolver被创建时就应该确保配置文件已经存在
 */
class ConfigService private constructor() {
    //对象化的配置
    lateinit var config: Config
    //配置文件
    private val file = File("forward.yml")
    //文件的内容
    private var content = ""
    companion object{
        /**
         * 创建ConfigService实例
         */
        fun create(): ConfigService = runBlocking{
            val instance = ConfigService()
            instance.apply {
                //如果没有配置文件则新建一个,并写入默认配置
                if (!file.exists()){
                    withContext(Dispatchers.IO) {
                        file.createNewFile()
                        file.writeText(defaultConfig)
                        logger.info("不存在配置文件->写入默认配置")
                        println()
                    }
                }
                withContext(Dispatchers.IO){
                    content = file.readText()
                }
                withContext(Dispatchers.Default){
                    config = decodeFromString(Config.serializer(),content)
                }
            }
            return@runBlocking instance
        }
    }
    /**
     * 保存配置文件
     */
    fun save() {
        GlobalScope.launch {
            try {
                withContext(Dispatchers.Default){
                    //序列化
                    content = encodeToString(Config.serializer(),config)
                }
                withContext(Dispatchers.IO){
                    //写入文件
                    file.writeText(content)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    /**
     * 实现从配置中加载保存的bot对象的登陆操作
     * 登陆交由 BotLoginSolver 实现
     */
    fun load(){
        logger.info("从配置中准备加载${config.botList.size-1}个Bot")
        config.botList.forEach {
            BotLoginSolver.autoLogin(it)
        }
        Forward.botDispatcher.changeTarget(config.target)
    }
}