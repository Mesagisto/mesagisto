import org.meowcat.minecraft.forward.chunkedHexToBytes
import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.data.Config
import org.meowcat.minecraft.forward.encodeToString
import org.meowcat.minecraft.forward.md5

suspend fun main(){
    val config = Config(
            listOf(Agent(123456789L,"123456789")),
            123456789L)
    print(encodeToString(Config.serializer(),config))
}