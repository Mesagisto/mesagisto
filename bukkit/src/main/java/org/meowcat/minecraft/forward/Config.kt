package org.meowcat.minecraft.forward

import kotlinx.serialization.Serializable

@Serializable
data class Config(val middlemanList: List<Middleman>,var crypto:Boolean)

