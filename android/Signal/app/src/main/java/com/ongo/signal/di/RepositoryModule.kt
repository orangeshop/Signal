package com.ongo.signal.di

import com.ongo.signal.data.repository.auth.AuthRepository
import com.ongo.signal.data.repository.auth.AuthRepositoryImpl
import com.ongo.signal.data.repository.match.MatchRepository
import com.ongo.signal.data.repository.match.MatchRepositoryImpl
import com.ongo.signal.data.repository.user.UserRepository
import com.ongo.signal.data.repository.user.UserRepositoryImpl
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

    @Singleton
    @Binds
    fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

}