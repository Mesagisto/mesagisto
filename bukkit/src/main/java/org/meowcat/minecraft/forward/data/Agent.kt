package org.meowcat.minecraft.forward.data

import kotlinx.serialization.Serializable

/**
 * @param account 帐号
 * @param password MD5密码
 */
@Serializable
data class Agent(var account:String,var password:String) {
    constructor(account: Long,password: String):this(account.toString(),password)
}