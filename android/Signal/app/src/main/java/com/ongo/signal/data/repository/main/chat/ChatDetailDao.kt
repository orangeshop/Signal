package com.ongo.signal.data.repository.main.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO


@Dao
interface ChatDetailDao {
    @Query("SELECT * FROM chat_detail_table where chat_id = :id")
    suspend fun getAll(id : Int): List<ChatHomeChildDto>

    @Insert()
    suspend fun insertMessage(ChatHomeChildDtos: ChatHomeChildDto)


}