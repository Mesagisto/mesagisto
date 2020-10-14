package org.meowcat.minecraft.forward.data

import kotlinx.serialization.Serializable

@Serializable
data class Config(val middlemanList: List<Middleman>, var crypto:Boolean)

