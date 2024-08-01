package com.ongo.signal.data.repository.chat.chatdatabase

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ongo.signal.data.model.chat.ChatHomeChildDto
import com.ongo.signal.data.model.chat.ChatHomeDTO
import com.ongo.signal.data.model.chat.DateConverter

//@Database(entities = [ChatHomeDTO::class, ChatHomeChildDto::class], version = 2, exportSchema = false)
//@TypeConverters(Converters::class)
@Database(
    version = 1,
    entities = [ChatHomeDTO::class, ChatHomeChildDto::class],

    exportSchema =false
)
@TypeConverters(DateConverter::class)
abstract class ChatHomeDatabase : RoomDatabase() {
    abstract fun chatHomeDao() : ChatHomeDao

    abstract fun chatDetailDao() : ChatDetailDao
}

