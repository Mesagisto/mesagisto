package org.meowcat.minecraft.forward.data

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import org.meowcat.minecraft.forward.*
import java.io.File

/*
    当一个ConfigSolver被创建时就应该确保配置文件已经存在
 */
class ConfigService() {

    //对象化的配置
    lateinit var config: Config
    //配置文件
    private val file = File("forward.yml")
    //文件的内容
    private var content = ""

    private var key = ""

    val keyChannel = Channel<String>()

    /**
     * 创建ConfigService实例
     */
    suspend fun create(): ConfigService {
        val instance = ConfigService()
        instance.apply {

            //这里会一直阻塞直到用户输入密匙
            key = keyChannel.receive()
            //如果没有配置文件则新建一个,并写入默认配置
            if (!file.exists()){
                withContext(Dispatchers.IO) {
                    file.createNewFile()
                    file.writeText(defaultConfig)
                }
            }
        }
        return instance
    }

    //保存配置文件
    suspend fun save(){
        //如果没加密则需要加密
        if (!config.crypto) encrypt()

        withContext(Dispatchers.Default) {
            content = Yaml.default.encodeToString(Config.serializer(),config)
        }
        //写入文件
        withContext(Dispatchers.IO) {
            file.writeText(content)
        }
    }
    /**
     * 加密
     */
    private suspend fun encrypt(){

        //如果已经加密则直接返回
        if (config.crypto) return

        config.crypto = true
        //TODO 加密
        withContext(Dispatchers.Default){
            for (agent in config.agentList){
                agent.account = agent.account.encrypt(key)
            }
        }

    }
    /**
     * 解密
     */
    private suspend fun decrypt(){
        //若已解密则直接返回
        if (!config.crypto) return
        config.crypto = false
        //TODO 解密
        withContext(Dispatchers.Default){
            for (agent in config.agentList){
                agent.account = agent.account.decrypt(key)
            }
        }

    }
    /**
     * 改变密匙用
     */
    suspend fun changeKey(newKey:String){
        if (key=="DEFAULT")
        //若已加密则解密
        if (config.crypto) decrypt()
        this.key = newKey
    }

}