package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ongo.signal.data.model.chat.ChatHomeChildDto


@Dao
interface ChatDetailDao {
    @Query("SELECT * FROM chat_detail_table where chat_id = :id")
    suspend fun getAll(id : Int): List<ChatHomeChildDto>

    @Insert()
    suspend fun insertMessage(ChatHomeChildDtos: ChatHomeChildDto)


}