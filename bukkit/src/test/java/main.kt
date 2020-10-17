import org.meowcat.minecraft.forward.chunkedHexToBytes
import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.data.Config
import org.meowcat.minecraft.forward.encodeToString
import org.meowcat.minecraft.forward.md5

suspend fun main(){
    val config = Config(mutableListOf(Agent(121313,"23232")),23232)
    config.botList.add(Agent(34341313,"23232"))
    println(encodeToString(Config.serializer(),config))
}