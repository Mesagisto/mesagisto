package org.meowcat.minecraft.forward

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Middleman{

    @SerialName("LISTEN")
    @Serializable
    data class Listen(var account:String,
                      var password:String,
                      var listen: String): Middleman()

    @SerialName("SPEAK")
    @Serializable
    data class Speak(var account:String,
                       var password:String,
                       var speak: String): Middleman()

    @SerialName("BOTH")
    @Serializable
    data class Both(var account:String,
                       var password:String): Middleman()

    @SerialName("NONE")
    @Serializable
    data class None(var account:String,
                    var password:String): Middleman()
}