package org.meowcat.minecraft.forward.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Agent(
        var account:String,
        var password:String) {
    constructor(account: Long,password: String) {
        this.account = account.toString()
        this.password = password
    }
}