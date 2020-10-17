package org.meowcat.minecraft.forward.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        suspend fun create(): ConfigService{
            val instance = ConfigService()
            instance.apply {
                //如果没有配置文件则新建一个,并写入默认配置
                if (!file.exists()){
                    withContext(Dispatchers.IO) {
                        file.createNewFile()
                        file.writeText(defaultConfig)
                    }
                }
                withContext(Dispatchers.IO){
                    content = file.readText()
                }
                withContext(Dispatchers.Default){
                    config = decodeFromString(Config.serializer(),content)
                }
            }
            return instance
        }
    }
    /**
     * 保存配置文件
     */
    suspend fun save()  {
        //序列化
        withContext(Dispatchers.Default) {
            content = encodeToString(Config.serializer(),config)
        }
        //写入文件
        withContext(Dispatchers.IO) {
            file.writeText(content)
        }
    }

    /**
     * 实现从配置中加载保存的bot对象的登陆操作
     * 登陆交由 BotLoginSolver 实现
     */
    suspend fun load(){
        config.botList.forEach {
            BotLoginSolver.autoLogin(it)
        }
    }
}