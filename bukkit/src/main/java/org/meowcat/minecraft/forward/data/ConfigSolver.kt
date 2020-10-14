package org.meowcat.minecraft.forward.data

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.meowcat.minecraft.forward.Forward
import java.io.File

class ConfigSolver() {
    companion object Factory{
        fun create(): ConfigSolver {
            return ConfigSolver().also {
                if (!it.file.exists()){
                    GlobalScope.launch(Dispatchers.IO) {
                        it.file.createNewFile()
                        it.content = File("resources/temple.yml").readText()
                        it.file.writeText(it.content)
                        it.config = Yaml.default.decodeFromString(Config.serializer(),it.file.readText())
                    }
                }
            }
        }
    }
    //对象化的配置
    lateinit var config: Config
    //配置文件
    private val file = File("forward.yml")
    //文件的内容
    private var content = ""
    //保存配置文件
    suspend fun save(){
        withContext(Dispatchers.IO) {
            if (Forward.listeners.isNotEmpty()) {
                for (listener in Forward.listeners) {

                }
            }
        }

        withContext(Dispatchers.IO) {
            file.writeText(content)
        }
    }
    //

}