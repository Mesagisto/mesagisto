package org.meowcat.minecraft.forward.data

import kotlinx.serialization.Serializable

@Serializable
class  Config(val agentList: List<Agent>,
              var target:Long = 12345678L)

