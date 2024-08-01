package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "chat_detail_table")
data class ChatHomeChildDto(
    @PrimaryKey(autoGenerate = false) var message_id: Long,
    val chat_id: Long,
    val is_from_sender: Boolean,
    val content : String,
    val is_read : Boolean,
    var send_at : String
)