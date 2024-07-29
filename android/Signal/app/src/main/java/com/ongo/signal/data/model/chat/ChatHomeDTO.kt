package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_home_table")
data class ChatHomeDTO(
    @PrimaryKey(autoGenerate = true) var chat_id: Int,
    val from_id: Int,
    val to_id: Int,
    val last_message: String,
    val sender_type: String
)