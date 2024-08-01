package com.ongo.signal.di

import com.ongo.signal.data.repository.chat.ChatRoomRepositoryImpl
import com.ongo.signal.data.repository.chat.ChatUseCases
import com.ongo.signal.data.repository.chat.ChatUseCasesImpl
import com.ongo.signal.data.repository.chat.chatservice.ChatRepository
import com.ongo.signal.network.StompService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideChatUseCases(
        chatRoomRepositoryImpl: ChatRoomRepositoryImpl,
        stompService: StompService,
        chatRepository: ChatRepository
    ): ChatUseCases {
        return ChatUseCasesImpl(chatRoomRepositoryImpl, stompService, chatRepository)
    }
}