package io.github.itsusinn.easyforward.bukkit.data

import kotlinx.serialization.Serializable

/**
 * @param botList 所有的bot的列表
 * @param target 目标群聊
 */
@Serializable
class Config(val botList: ArrayList<Agent>,
             val creators:HashMap<Long,String>,
             var target:Long,
             var smms:String)
