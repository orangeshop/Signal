package com.ongo.signal.data.repository.main

import android.content.Context
import androidx.room.Room
import com.ongo.signal.data.repository.main.chat.ChatHomeDao
import com.ongo.signal.data.repository.main.chat.ChatHomeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): ChatHomeDatabase {
        return Room.databaseBuilder(
            appContext,
            ChatHomeDatabase::class.java,
            "chat_home_database"
        ).build()
    }

    @Provides
    fun provideChatHomeDao(database: ChatHomeDatabase):ChatHomeDao{
        return database.chatHomeDao()
    }
}