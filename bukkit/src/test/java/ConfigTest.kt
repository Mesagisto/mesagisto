import org.meowcat.minecraft.forward.data.Agent
import org.meowcat.minecraft.forward.data.Config
import org.meowcat.minecraft.forward.encodeToString

fun main(){
   val defaultConfig = Config(
      arrayListOf(Agent(123456789L, "9A0364B9E99BB480DD25E1F0284C8555")),
      hashMapOf(123456789L to "CONSOLE"),
      123456789L,
      "4rllP8FcldmAJ726VqqCSNFKkvSNW4MG"
   )
   println(encodeToString(Config.serializer(), defaultConfig))
}