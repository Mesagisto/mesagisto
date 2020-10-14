import com.charleskorn.kaml.Yaml
import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.data.Config

fun main(){
    val list = listOf(
            Agent("12345678","12345678")
    )
    val content = Config(list,false,)
    val result = Yaml.default.encodeToString(Config.serializer(),content)
    println(result)

}
