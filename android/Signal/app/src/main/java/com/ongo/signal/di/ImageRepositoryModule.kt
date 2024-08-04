package com.ongo.signal.di

import com.ongo.signal.data.repository.main.image.ImageRepository
import com.ongo.signal.data.repository.main.image.ImageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ImageRepositoryModule {

    @Singleton
    @Binds
    fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository
}