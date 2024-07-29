package com.ongo.signal.di

import com.ongo.signal.data.repository.chat.ChatRoomRepository
import com.ongo.signal.data.repository.chat.ChatUseCases
import com.ongo.signal.data.repository.chat.ChatUseCasesImpl
import com.ongo.signal.network.StompService
import com.ongo.signal.ui.chat.ChatHomeViewModel
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
        chatRoomRepository: ChatRoomRepository,
        stompService: StompService
    ): ChatUseCases {
        return ChatUseCasesImpl(chatRoomRepository, stompService)
    }
}