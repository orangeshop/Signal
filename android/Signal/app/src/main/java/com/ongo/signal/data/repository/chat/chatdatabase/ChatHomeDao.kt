package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ongo.signal.data.model.chat.ChatHomeDTO


@Dao
interface ChatHomeDao {
    @Query("SELECT * FROM chat_home_table where fromId = :id or toId = :id")
    suspend fun getAll(id : Long): List<ChatHomeDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg chatHomeDTOs: ChatHomeDTO)

    @Query("DELETE FROM chat_home_table WHERE chatId = :id")
    suspend fun delete(id : Long)
}