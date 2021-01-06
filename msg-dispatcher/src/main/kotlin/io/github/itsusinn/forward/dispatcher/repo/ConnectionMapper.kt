package io.github.itsusinn.forward.dispatcher.repo

import io.vertx.core.http.ServerWebSocket

/**
 * @property identifierAddressMapper <identifier> - <address>
 * @property identifierInstanceMapper <identifier> - <ws instance>
 * @property addressIdentifierMapper <address> - <list of identifiers>
 */
class ConnectionMapper{

   private val identifierAddressMapper = HashMap<String, String>()
   private val identifierInstanceMapper = HashMap<String, ServerWebSocket>()
   private val instanceIdentifierMapper = HashMap<ServerWebSocket, String>()
   private val addressIdentifierMapper = HashMap<String, ArrayList<String>>()

   /**
    * @param address publish address,spliced by "appID:channelID"
    * @param instance ws instance
    * @param identifier the id of ws,usually binaryHandlerID or hashcode
    */
   fun save(address: String, instance: ServerWebSocket, identifier: String){
      identifierAddressMapper[identifier] = address
      identifierInstanceMapper[identifier] = instance
      instanceIdentifierMapper[instance] = identifier
      addressIdentifierMapper.getOrPut(address){ ArrayList<String>() }.add(identifier)
   }

   fun findAddressById(identifier: String):String {
      return identifierAddressMapper[identifier]
         ?: throw NullPointerException()
   }

   fun findIdByInstance(instance: ServerWebSocket): String {
      return instanceIdentifierMapper[instance]
         ?: throw NullPointerException()
   }

   fun findInstanceById(identifier:String): ServerWebSocket? {
      return identifierInstanceMapper[identifier]
   }

   fun findIdListByAddress(address:String): List<String> {
      return addressIdentifierMapper[address]
         ?: throw NullPointerException("Cannot get the ws id list of specific address")
   }

   fun closeByInstance(ws: ServerWebSocket) {
      val id = instanceIdentifierMapper.remove(ws)
         ?: throw NullPointerException("Cannot get the ws id of specific instance")
      identifierInstanceMapper.remove(id,ws)
      val address = identifierAddressMapper.remove(id)
         ?: throw NullPointerException("Cannot get the ws address of specific id")
      val wsIDList = addressIdentifierMapper[address]
         ?: throw NullPointerException("Cannot get the ws id list of specific address")
      wsIDList.remove(id)
      if (wsIDList.isEmpty()) addressIdentifierMapper.remove(address)
   }
}