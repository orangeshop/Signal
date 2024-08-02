package com.ongo.signal.di

import com.ongo.signal.data.repository.chat.ChatRoomRepositoryImpl
import com.ongo.signal.data.repository.chat.chatdatabase.ChatDetailDao
import com.ongo.signal.data.repository.chat.chatdatabase.ChatHomeDao
import com.ongo.signal.data.repository.chat.chatdatabase.ChatHomeLocalCheckDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideChatRepository(
        chatHomeDao: ChatHomeDao,
        chatDetailDao: ChatDetailDao,
        chatHomeLocalCheckDao: ChatHomeLocalCheckDao
    ): ChatRoomRepositoryImpl {
        return ChatRoomRepositoryImpl(chatHomeDao, chatDetailDao, chatHomeLocalCheckDao)
    }

}