package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ongo.signal.data.model.chat.ChatHomeChildDTO


@Dao
interface ChatDetailDao {
    @Query("SELECT * FROM chat_detail_table where chatId = :id order by messageId desc limit :loading")
    suspend fun getAll(id : Long, loading: Long): List<ChatHomeChildDTO>

    @Query("SELECT * FROM chat_detail_table order by messageId desc limit :loading")
    suspend fun getAllMessage(loading: Long): List<ChatHomeChildDTO>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chatHomeChildDtos: ChatHomeChildDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListMessage(chatHomeChildDtos: List<ChatHomeChildDTO>)

    @Query("select count(*) from chat_detail_table where chatId = :id and isRead = 0")
    suspend fun loadReadMessage(id : Long) : Int
}