package org.meowcat.minecraft.forward

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

class ConfigSolver() {
    companion object Factory{
        suspend fun create(): ConfigSolver {
            return ConfigSolver().also {
                if (!it.file.exists()){
                    withContext(Dispatchers.IO){
                        it.file.createNewFile()
                        it.file.writeText(File("resources/temple.yml").readText())
                    }
                }
            }
        }
    }

    val config: Config by lazy {
        Yaml.default.decodeFromString(Config.serializer(),file.readText())
    }

    private val file = File("forward.yml")
    private val content = ""
    //保存配置文件
    suspend fun save(){
        withContext(Dispatchers.Default){
            if (Forward.listeners.isNotEmpty()){
                for (listener in Forward.listeners){

                }
            }
        }

        withContext(Dispatchers.IO){
            if (!file.exists()) file.createNewFile()
            file.writeText(content)
        }
    }
    //加载配置文件
    suspend fun load():Boolean{
        if (!file.exists()){
            withContext(Dispatchers.IO){
                file.createNewFile()
                logger.info("配置文件不存在 - 已自动创建")
            }
            return false
        }
        return true
    }
}
@Serializable
data class Config(val middlemanList: List<Middleman>)

@Serializable
sealed class Middleman{

    @SerialName("LISTEN")
    @Serializable
    data class Listen(val account:String,
                      var password:String,
                      val listen: Long): Middleman()

    @SerialName("SPEAK")
    @Serializable
    data class Speak(val account:String,
                       var password:String,
                       val speak: Long):Middleman()

    @SerialName("BOTH")
    @Serializable
    data class Both(val account:String,
                       var password:String):Middleman()

    @SerialName("NONE")
    @Serializable
    data class None(val account:String,
                    var password:String):Middleman()
}
