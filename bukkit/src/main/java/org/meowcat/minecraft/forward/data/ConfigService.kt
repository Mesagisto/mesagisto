package org.meowcat.minecraft.forward.data

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.meowcat.minecraft.forward.Forward
import java.io.File
import java.net.URL
/*
    当一个ConfigSolver被创建时就应该确保配置文件已经存在
 */
class ConfigService() {
    companion object Factory{
        fun create(): ConfigService {
            return ConfigService().apply {
                if (!file.exists()){

                    GlobalScope.launch(Dispatchers.IO) {
                        file.createNewFile()
                        content
                        file.writeText(content)
                        config = Yaml.default.decodeFromString(Config.serializer(),file.readText())
                    }
                    TODO("初始化过程")
                }
            }
        }
    }

    private lateinit var url: URL

    //对象化的配置
    lateinit var config: Config
    //配置文件
    private val file = File("forward.yml")
    //文件的内容
    private var content = ""
    //保存配置文件
    suspend fun save(){
        withContext(Dispatchers.IO) {
            if (Forward.allBots.isNotEmpty()) {
                for (bot in Forward.allBots) {
                    config.agentList
                    TODO("保存")
                }
            }
        }

        withContext(Dispatchers.IO) {
            file.writeText(content)
        }
    }
    //加密
    private fun encrypt(){
        //如果已经加密则直接返回
        if (config.crypto) return
        config.crypto = true
        TODO("加密")

    }
    //解密
    private fun decrypt(){
        //若已解密则直接返回
        if (!config.crypto) return
        config.crypto = false
        TODO("解密")

    }
    //
    private fun changeKey(){
        TODO("改变密匙")
    }

}