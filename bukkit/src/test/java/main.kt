import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.data.Config
import org.meowcat.minecraft.forward.encodeToString

suspend fun main(){
    val config = Config(mutableListOf(Agent(121313,"23232")) as ArrayList<Agent>,23232)
    config.botList.add(Agent(34341313,"23232"))
    println(encodeToString(Config.serializer(),config))
    val map = HashMap<String,String>()
    map.keys
}