package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ongo.signal.data.model.chat.ChatHomeChildDto


@Dao
interface ChatDetailDao {
    @Query("SELECT * FROM chat_detail_table where chat_id = :id")
    suspend fun getAll(id : Long): List<ChatHomeChildDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(ChatHomeChildDtos: ChatHomeChildDto)
}