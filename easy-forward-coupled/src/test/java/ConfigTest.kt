import io.github.itsusinn.mc.easyforward.data.Agent
import io.github.itsusinn.mc.easyforward.data.Config
import io.github.itsusinn.mc.easyforward.extension.encodeToString

fun main(){
   val defaultConfig = Config(
      arrayListOf(Agent(123456789L, "9A0364B9E99BB480DD25E1F0284C8555")),
      hashMapOf(123456789L to "CONSOLE"),
      123456789L,
      "4rllP8FcldmAJ726VqqCSNFKkvSNW4MG"
   )
   println(encodeToString(Config.serializer(), defaultConfig))
}