package com.ongo.signal.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ongo.signal.network.SignalApi
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
    fun provideSignalRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.100.161:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideGptApiService(retrofit: Retrofit): SignalApi =
        retrofit.create(SignalApi::class.java)
}