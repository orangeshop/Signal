package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "chat_detail_table")
data class ChatHomeChildDto(
    @PrimaryKey(autoGenerate = true)var message_id: Int,
    val chat_id: Int,
    val userKey: Int,
    val user2Key: Int,
    val is_from_sender: Boolean,
    val content : String,
    val read : Boolean,
    val send_at : String
)