package com.ongo.signal.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.network.LoginApi
import com.ongo.signal.network.MainApi
import com.ongo.signal.network.MatchApi
import com.ongo.signal.data.repository.chat.chatservice.ChatRepository
import com.ongo.signal.data.repository.chat.chatservice.ChatRepositoryImpl
import com.ongo.signal.network.ChatRoomApi
import com.ongo.signal.network.StompService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    @Singleton
    @Provides
    fun provideSignalRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://13.125.47.74:8080/")  // EC2
//        .baseUrl("http://192.168.100.161:8080/") // 병현서버
//        .baseUrl("http://192.168.100.95:8080/") // 인수서버
//        .baseUrl("http://192.168.100.200:8080/") // 민수서버
        
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideLoginApiService(retrofit: Retrofit): LoginApi =
        retrofit.create(LoginApi::class.java)

    @Singleton
    @Provides
    fun provideMatchApiService(retrofit: Retrofit): MatchApi =
        retrofit.create(MatchApi::class.java)

    @Singleton
    @Provides
    fun provideMainApiService(retrofit: Retrofit): MainApi =
        retrofit.create(MainApi::class.java)

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