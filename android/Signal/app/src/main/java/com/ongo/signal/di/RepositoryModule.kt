package com.ongo.signal.di

import com.ongo.signal.data.repository.SignalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindRepository(
        signalRepository: SignalRepository
    ): SignalRepository
}