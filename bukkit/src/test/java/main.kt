import org.meowcat.minecraft.forward.chunkedHexToBytes
import org.meowcat.minecraft.forward.md5

fun main(){
    "abc".md5.chunkedHexToBytes().forEach {
        println(it)
    }
}