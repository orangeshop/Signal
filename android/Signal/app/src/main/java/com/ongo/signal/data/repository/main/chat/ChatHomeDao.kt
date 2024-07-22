package com.ongo.signal.data.repository.main.chat

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO


@Dao
interface ChatHomeDao {
    @Query("SELECT * FROM chat_home_room")
    suspend fun getAll(): List<ChatHomeDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg chatHomeDTOs: ChatHomeDTO)

    @Query("SELECT * FROM chat_home_room WHERE id = :ID")
    suspend fun getList(ID:Int): ChatHomeDTO


}