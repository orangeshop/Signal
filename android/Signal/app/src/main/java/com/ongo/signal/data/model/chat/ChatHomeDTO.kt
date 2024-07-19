package com.ongo.signal.data.model.chat

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey

//data class ChatHomeDTO(var id: Int, val list: MutableList<ChatHomeChildDto>)
@Entity(tableName = "chat_home_room")
data class ChatHomeDTO (
    @PrimaryKey var id: Int,
    val list: MutableList<ChatHomeChildDto>
)