package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_home_table")
data class ChatHomeDTO(
    @PrimaryKey(autoGenerate = true) var chat_id: Long,
    val from_id: Long,
    val to_id: Long,
    val from_name: String,
    val to_name: String,
    val last_message: String,
    val sender_type: String,
    var send_at: Date
)