@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.github.itsusinn.mc.easyforward.extension

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import java.security.MessageDigest

/**
 * 从String中反序列化为KotlinObject
 * @param deserializer @Serializer注解生成的反序列化器
 * @param string 需要反序列化的字符串
 * @author Itsusinn
 */
fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T{
   return Yaml.default.decodeFromString(deserializer, string)
}

/**
 * 将KotlinObject序列化为String
 * @param serializer @Serializer注解生成的序列化器
 * @param value 需要序列化的KotlinObject
 * @author Itsusinn
 */
fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
   return Yaml.default.encodeToString(serializer, value)
}

/**
 * 拓展属性 计算字符串的MD5
 * From https://github.com/ryoii/mirai-console-addition
 * @author ryoii
 */
val String.md5:String
    get() = MessageDigest.getInstance("MD5").
    apply { update(this@md5.toByteArray()) }.digest().toUHexString("")

/**
 * 拓展方法 将十六进制字符串转为ByteArray
 * From https://github.com/ryoii/mirai-console-addition
 * @author ryoii
 */
internal fun String.chunkedHexToBytes(): ByteArray =
        this.asSequence().chunked(2).map { (it[0].toString() + it[1]).toUByte(16).toByte() }.toList().toByteArray()

/**
 * From https://github.com/ryoii/mirai-console-addition
 * @author ryoii
 */
@JvmOverloads
@Suppress("DuplicatedCode")
internal fun ByteArray.toUHexString(separator: String = " ", offset: Int = 0, length: Int = this.size - offset): String {
    if (length == 0) {
        return ""
    }
    val lastIndex = offset + length
    return buildString(length * 2) {
        this@toUHexString.forEachIndexed { index, it ->
            if (index in offset until lastIndex) {
                var ret = it.toUByte().toString(16).toUpperCase()
                if (ret.length == 1) ret = "0$ret"
                append(ret)
                if (index < lastIndex - 1) append(separator)
            }
        }
    }
}
