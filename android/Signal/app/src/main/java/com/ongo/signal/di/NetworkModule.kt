package com.ongo.signal.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.network.MainApi
import com.ongo.signal.network.SignalApi
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
        .baseUrl("http://192.168.100.200:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideGptApiService(retrofit: Retrofit): SignalApi =
        retrofit.create(SignalApi::class.java)

    @Singleton
    @Provides
    fun provideMainApiService(retrofit: Retrofit): MainApi =
        retrofit.create(MainApi::class.java)

    @Provides
    @Singleton
    fun provideStompService(): StompService {
        return StompService()
    }
}