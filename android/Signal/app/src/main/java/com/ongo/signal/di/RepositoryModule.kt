package com.ongo.signal.di

import com.ongo.signal.data.repository.user.UserRepository
import com.ongo.signal.data.repository.user.UserRepositoryImpl
import com.ongo.signal.data.repository.match.MatchRepository
import com.ongo.signal.data.repository.match.MatchRepositoryImpl
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
    fun bindSignalRepository(
        signalRepositoryImpl: MatchRepositoryImpl
    ): MatchRepository

    @Singleton
    @Binds
    fun bindLoginRepository(
        loginRepositoryImpl: UserRepositoryImpl
    ): UserRepository

}