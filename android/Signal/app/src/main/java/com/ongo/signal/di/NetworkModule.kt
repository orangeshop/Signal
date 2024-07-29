package com.ongo.signal.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.data.repository.chat.chatservice.ChatRepository
import com.ongo.signal.data.repository.chat.chatservice.ChatRepositoryImpl
import com.ongo.signal.network.ChatRoomApi
import com.ongo.signal.network.SignalApi
import com.ongo.signal.network.StompService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()
    

    @Singleton
    @Provides
    fun provideSignalRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.100.95:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideGptApiService(retrofit: Retrofit): SignalApi =
        retrofit.create(SignalApi::class.java)


    @Provides
    @Singleton
    fun provideStompService(): StompService {
        return StompService()
    }

    @Provides
    @Singleton
    fun provideChatRoomApi(retrofit: Retrofit): ChatRoomApi {
        return retrofit.create(ChatRoomApi::class.java)
    }


    @Provides
    @Singleton
    fun provideChatRepository(chatApi: ChatRoomApi): ChatRepository {
        return ChatRepositoryImpl(chatApi)
    }
}