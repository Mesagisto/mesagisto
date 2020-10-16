package org.meowcat.minecraft.forward.data

import kotlinx.serialization.Serializable

@Serializable
data class Agent(var account:String,var password:String) {
    constructor(account: Long,password: String):this(account.toString(),password)
}