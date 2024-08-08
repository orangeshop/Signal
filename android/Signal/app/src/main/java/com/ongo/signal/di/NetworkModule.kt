package com.ongo.signal.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.data.repository.chat.chatservice.ChatRepository
import com.ongo.signal.data.repository.chat.chatservice.ChatRepositoryImpl
import com.ongo.signal.network.ChatRoomApi
import com.ongo.signal.network.MainApi
import com.ongo.signal.network.MatchApi
import com.ongo.signal.network.MyPageApi
import com.ongo.signal.network.ReviewApi
import com.ongo.signal.network.StompService
import com.ongo.signal.network.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
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
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .build()

    @Singleton
    @Provides
    @Named("signal")
    fun provideSignalRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://13.125.47.74:8080/")  // EC2
//        .baseUrl("http://192.168.100.161:8080/") // 병현서버
//        .baseUrl("http://192.168.100.95:8080/") // 인수서버
//        .baseUrl("http://192.168.100.200:8080/") // 민수서버
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    @Named("auth")
    fun provideAuthRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://13.125.47.74:8080/")  // EC2
//        .baseUrl("http://192.168.100.161:8080/") // 병현서버
//        .baseUrl("http://192.168.100.95:8080/") // 인수서버
//        .baseUrl("http://192.168.100.200:8080/") // 민수서버
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideUserApiService(@Named("signal") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun provideMatchApiService(@Named("signal") retrofit: Retrofit): MatchApi =
        retrofit.create(MatchApi::class.java)

    @Singleton
    @Provides
    fun provideMainApiService(@Named("signal")retrofit: Retrofit): MainApi =
        retrofit.create(MainApi::class.java)

    @Provides
    @Singleton
    fun provideStompService(): StompService {
        return StompService()
    }

    @Provides
    @Singleton
    fun provideChatRoomApi(@Named("signal") retrofit: Retrofit): ChatRoomApi {
        return retrofit.create(ChatRoomApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatApi: ChatRoomApi): ChatRepository {
        return ChatRepositoryImpl(chatApi)
    }

    @Provides
    @Singleton
    fun provideMyPageRepository(@Named("signal") retrofit: Retrofit): MyPageApi =
        retrofit.create(MyPageApi::class.java)

    @Provides
    @Singleton
    fun provideReviewRepository(@Named("signal") retrofit: Retrofit): ReviewApi =
        retrofit.create(ReviewApi::class.java)
}