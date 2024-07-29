package com.ongo.signal.di

import com.ongo.signal.data.repository.login.LoginRepository
import com.ongo.signal.data.repository.login.LoginRepositoryImpl
import com.ongo.signal.data.repository.match.SignalRepository
import com.ongo.signal.data.repository.match.SignalRepositoryImpl
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
        signalRepositoryImpl: SignalRepositoryImpl
    ): SignalRepository

    @Singleton
    @Binds
    fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

}