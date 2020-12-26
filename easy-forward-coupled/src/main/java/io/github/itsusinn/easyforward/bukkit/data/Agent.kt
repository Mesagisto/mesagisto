package io.github.itsusinn.easyforward.bukkit.data

import kotlinx.serialization.Serializable

/**
 * @param account 帐号
 * @param passwordMD5 MD5密码
 */
@Serializable
data class Agent(
   var account:String,
   var passwordMD5:String) {

    constructor(account: Long,password: String): this(account.toString(),password)
}