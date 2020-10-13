import com.charleskorn.kaml.Yaml
import org.meowcat.minecraft.forward.Middleman
import org.meowcat.minecraft.forward.Config

fun main(){
    val list = listOf(
            Middleman.None("12345678","12345678")
    )
    val content = Config(list)
    val result = Yaml.default.encodeToString(Config.serializer(),content)
    println(result)

}