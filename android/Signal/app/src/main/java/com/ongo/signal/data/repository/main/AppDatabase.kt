package com.ongo.signal.data.repository.main

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.Converters
import com.ongo.signal.data.repository.main.chat.ChatHomeDao

@Database(entities = [ChatHomeDTO::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatHomeDao() : ChatHomeDao
}