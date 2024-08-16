package com.ongo.signal.di

import com.ongo.signal.data.repository.mypage.MyPageRepository
import com.ongo.signal.data.repository.mypage.MyPageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MyPageRepositoryModule {

    @Singleton
    @Binds
    fun myPageRepository(
        myPageRepositoryImpl: MyPageRepositoryImpl
    ): MyPageRepository
}