package com.ongo.signal.data.model.chat

import androidx.room.Entity
import androidx.room.PrimaryKey

//data class ChatHomeDTO(var id: Int, val list: MutableList<ChatHomeChildDto>)
@Entity(tableName = "chat_home_table")
data class ChatHomeDTO(
    @PrimaryKey(autoGenerate = true) var chat_id: Int,
    val userKey: Int,
    val user2Key: Int,
    val last_message: String,
    val status: String
)