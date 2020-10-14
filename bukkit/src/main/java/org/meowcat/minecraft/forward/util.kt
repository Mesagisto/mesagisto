package org.meowcat.minecraft.forward

import cn.hutool.core.util.CharsetUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.crypto.symmetric.SymmetricAlgorithm
import cn.hutool.crypto.symmetric.SymmetricCrypto

//aes加密
fun String.encrypt(key: String): String {
    //构建
    val aes = SymmetricCrypto(SymmetricAlgorithm.AES, DigestUtil.md5Hex(key).toByteArray())
    //加密为16进制表示
    return aes.encryptHex(aes.encrypt(this))
}
//aes加密
fun String.decrypt(key: String):String {
    //构建
    val aes = SymmetricCrypto(SymmetricAlgorithm.AES, DigestUtil.md5Hex(key).toByteArray())
    //解密
    return aes.decryptStr(this, CharsetUtil.CHARSET_UTF_8)
}