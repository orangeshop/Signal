package com.ongo.signal.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.network.SignalApi
import com.ongo.signal.network.StompService
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
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .connectTimeout(5000, TimeUnit.MILLISECONDS)
        .build()

    @Singleton
    @Provides
    fun provideSignalRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("baseurl")
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
}