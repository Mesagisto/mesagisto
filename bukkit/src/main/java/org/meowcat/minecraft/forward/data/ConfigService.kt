package org.meowcat.minecraft.forward.data

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.meowcat.minecraft.forward.Forward
import org.meowcat.minecraft.forward.decrypt
import org.meowcat.minecraft.forward.encrypt
import org.meowcat.minecraft.forward.launch
import java.io.File
import java.net.URL
/*
    当一个ConfigSolver被创建时就应该确保配置文件已经存在
 */
class ConfigService() {
    companion object Factory{
        fun create(key:String): ConfigService {
            val instance = ConfigService()
            instance.apply {
                if (!file.exists()){

                    this.key = key

                    GlobalScope.launch(Dispatchers.IO) {
                        file.createNewFile()
                        content = file.readText()
                        file.writeText(content)
                        config = Yaml.default.decodeFromString(Config.serializer(),content)
                    }
                    TODO("初始化过程")
                }
            }
            return instance
        }
    }

    //对象化的配置
    lateinit var config: Config
    //配置文件
    private val file = File("forward.yml")
    //文件的内容
    private var content = ""

    private var key = ""
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
    private suspend fun changeKey(newKey:String){
        //若已加密则解密
        if (config.crypto) decrypt()
        this.key = newKey
    }

}