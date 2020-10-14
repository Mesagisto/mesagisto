import cn.hutool.core.util.CharsetUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.crypto.symmetric.SymmetricAlgorithm
import cn.hutool.crypto.symmetric.SymmetricCrypto


//fun main(){
//    val list = listOf(
//            Middleman.None("12345678","12345678")
//    )
//    val content = Config(list)
//    val result = Yaml.default.encodeToString(Config.serializer(),content)
//    println(result)
//
//}

fun main(){

    // 5393554e94bf0eb6436f240a4fd71282
    val key = DigestUtil.md5Hex("password")

    val content = "test中文"
    //构建
    val aes = SymmetricCrypto(SymmetricAlgorithm.AES, key.toByteArray())
    //加密
    val encrypt = aes.encrypt(content)
    //解密
    val decrypt = aes.decrypt(encrypt)
    //加密为16进制表示
    val encryptHex = aes.encryptHex(content)
    println("encryptHex:$encryptHex")
    //解密为字符串
    val decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8)
    println("decrypt:$decryptStr")

}