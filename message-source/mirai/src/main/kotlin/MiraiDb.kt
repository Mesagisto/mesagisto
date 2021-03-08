package org.meowcat.mesagisto.mirai

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import net.mamoe.mirai.message.MessageSerializers
import net.mamoe.mirai.message.data.* // ktlint-disable no-wildcard-imports
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.bytes
import org.ktorm.support.sqlite.SQLiteDialect
import org.ktorm.support.sqlite.insertOrUpdate
import org.mesagisto.client.Db
import org.mesagisto.client.toByteArray
import java.io.File

object MessageSourceT : Table<Nothing>("message_source") {
  val id = bytes("id").primaryKey()
  val data = bytes("data")
  fun createTable(database: Database) {
    database.useConnection { conn ->
      conn.prepareStatement(
        """
        CREATE TABLE IF NOT EXISTS message_source(
           id blob PRIMARY KEY NOT NULL,
           data blob NOT NULL
        );
        """.trimIndent()
      ).execute()
    }
  }
}

object MiraiDb {
  private val database by lazy {
    File(Db.db_prefix).mkdirs()
    val database = Database.connect("jdbc:sqlite:${Db.db_prefix}/mirai.sqlite", dialect = SQLiteDialect())
    MessageSourceT.createTable(database)
    database
  }

  @OptIn(ExperimentalSerializationApi::class)
  private val MiraiProtoBuf = ProtoBuf {
    serializersModule = MessageSerializers.serializersModule
  }

  @OptIn(ExperimentalSerializationApi::class)
  fun putMsgSource(source: MessageSource) {
    database.insertOrUpdate(MessageSourceT) {
      val data = MiraiProtoBuf.encodeToByteArray(MessageSource.serializer(), source)
      set(it.id, source.targetId.toByteArray() + source.ids.first().toByteArray())
      set(it.data, data)
      onConflict {
        set(it.data, data)
      }
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  fun getMsgSource(target: Long, id: Int): MessageSource? {
    val data = database.from(MessageSourceT)
      .select()
      .where { MessageSourceT.id eq (target.toByteArray() + id.toByteArray()) }
      .map { it[MessageSourceT.data] }
      .firstOrNull() ?: return null
    return MiraiProtoBuf.decodeFromByteArray(MessageSource.serializer(), data)
  }
}
