package io.github.itsusinn.forward.dispatcher.repo

import io.github.itsusinn.extension.jackson.asPrettyString
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

//90 seconds
const val deadTime = 60*1000*1.5

private val logger = KotlinLogging.logger {  }

/**
 * this class stores all ws connections and their info
 *
 * @property idPosMapper <identifier> - <address>
 * @property idReferMapper <identifier> - <ws instance>
 * @property posIdMapper <address> - <list of identifiers>
 */
class ConnectionRepo(){

   //will be replaced by in-memory database
   private val idPosMapper = ConcurrentHashMap<String, String>()
   private val idReferMapper = ConcurrentHashMap<String, ServerWebSocket>()
   private val referIdMapper = ConcurrentHashMap<ServerWebSocket, String>()
   private val posIdMapper = ConcurrentHashMap<String, ArrayList<String>>()
   private val idAliveMapper = ConcurrentHashMap<String,Long>()

   fun status():String{
      val s = HashMap<String,Any>()
      s.put("idPosMapper",idPosMapper)
      s.put("idReferMapper",idReferMapper)
      s.put("referIdMapper",referIdMapper)
      s.put("posIdMapper",posIdMapper)
      s.put("idAliveMapper",idAliveMapper)
      return s.asPrettyString ?: "error inquiring status"
   }

   /**
    * @param pos message dispatch address,spliced by "appID:channelID"
    * @param refer ws instance
    * @param id the id of ws,usually binaryHandlerID or hashcode
    */
   fun save(pos: String, refer: ServerWebSocket, id: String){
      idPosMapper[id] = pos
      idReferMapper[id] = refer
      referIdMapper[refer] = id
      posIdMapper.getOrPut(pos){ArrayList<String>()}.add(id)
      idAliveMapper[id] = now()
   }
   private var iterator = idAliveMapper.iterator()
   private var max = 0

   fun setAutoClean(vertx: Vertx) = vertx.setPeriodic(5*60*1000){
      max = if (idAliveMapper.size > 5000) 5000 else idAliveMapper.size
      var processd = 0
      while (processd < max){
         processd++
         if (!iterator.hasNext()) {
            iterator = idAliveMapper.iterator()
            continue
         }
         val pair = iterator.next()
         if ((now() - pair.value) > deadTime){
            closeById(pair.key)
         }
      }
   }

   fun reAliveById(id: String){
      idAliveMapper[id] = now()
   }

   fun checkAliveByInstance(refer: ServerWebSocket):Boolean{
      val id = findIdByRefer(refer) ?: return false
      return checkAliveById(id)
   }

   fun checkAliveById(id: String):Boolean{
      val live = findAliveById(id) ?: return false
      return (now() - live) < deadTime
   }

   fun findPosById(id: String):String? =
      idPosMapper[id] ?: run {
         logger.warn { "Cannot find id:$id's address from identifierAddressMapper" }
         null
      }

   fun findAliveById(id:String):Long? =
      idAliveMapper[id] ?: run {
         logger.warn { "Cannot find id:$id's alive time" }
         null
      }

   fun findIdByRefer(refer: ServerWebSocket): String? =
      referIdMapper[refer] ?: run {
          logger.warn { "Cannot find instance:$refer's id" }
          null
       }

   fun findInstanceById(id:String): ServerWebSocket? =
      idReferMapper[id] ?: run{
         logger.warn { "Cannot find id:$id's instance" }
         null
      }

   fun findIdListByAddress(address:String): List<String>?{
      val list = findIdListByAddressWithNoCopy(address)?: return null
      val copy = ArrayList<String>()
      copy.addAll(list)
      return copy
   }
   fun findIdListByAddressWithNoCopy(address:String): ArrayList<String>?{
      val list = posIdMapper[address] ?: run {
         logger.warn { "Cannot find address:$address's wsIDList from addressIdentifierMapper" }
         return null
      }
      return list
   }

   fun closeById(id:String){
      idAliveMapper.remove(id)
      removeReferById(id)
      removePosById(id)
   }
   private fun removeReferById(id:String){
      val refer = findInstanceById(id) ?: return
      idReferMapper.remove(id)
      referIdMapper.remove(refer)
   }
   private fun removePosById(id: String){
      val address = findPosById(id) ?: return
      idPosMapper.remove(id)

      val wsIDList = findIdListByAddressWithNoCopy(address) ?: return
      wsIDList.remove(id)

      //when the address's ws-id is empty,remove it
      if (wsIDList.isEmpty()) posIdMapper.remove(address)
   }
}

private fun now() = System.currentTimeMillis()