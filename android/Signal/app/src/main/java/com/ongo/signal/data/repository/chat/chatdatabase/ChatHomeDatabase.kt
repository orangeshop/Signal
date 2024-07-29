package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ongo.signal.data.model.chat.ChatHomeDTO

@Database(entities = [ChatHomeDTO::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class ChatHomeDatabase : RoomDatabase() {
    abstract fun chatHomeDao() : ChatHomeDao
}

