package org.meowcat.minecraft.forward

import cn.hutool.core.util.CharsetUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.crypto.symmetric.SymmetricAlgorithm
import cn.hutool.crypto.symmetric.SymmetricCrypto
import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

/**
 * aes加密
 * @param key 密匙
 */
fun String.encrypt(key: String): String {
    //构建
    val aes = SymmetricCrypto(SymmetricAlgorithm.AES, DigestUtil.md5Hex(key).toByteArray())
    //加密为16进制表示
    return aes.encryptBase64(this)
}
/**
 * aes解密
 * @param key 密匙
 */
fun String.decrypt(key: String):String {
    //构建
    val aes = SymmetricCrypto(SymmetricAlgorithm.AES, DigestUtil.md5Hex(key).toByteArray())

    //解密
    return aes.decryptStr(this, CharsetUtil.CHARSET_UTF_8)
}
/**
 * aes加密
 * @param key 密匙
 */
fun Any.encrypt(key: String): String {
    //加密为16进制表示
    return this.toString().encrypt(key)
}

/**
 * 从String中反序列化为KotlinObject
 * @param deserializer @Serializer注解生成的反序列化器
 * @param string 需要反序列化的字符串
 * @author Itsusinn
 */
suspend fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T{
    val result:T
    withContext(Dispatchers.Default){
        result = Yaml.default.decodeFromString(deserializer,string)
    }
    return result
}

/**
 * 将KotlinObject序列化为String
 * @param serializer @Serializer注解生成的序列化器
 * @param value 需要序列化的KotlinObject
 * @author Itsusinn
 */
suspend fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
    val result:String
    withContext(Dispatchers.Default){
        result = Yaml.default.encodeToString(serializer,value)
    }
    return result
}