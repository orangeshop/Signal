package com.ongo.signal.di

import com.ongo.signal.data.repository.review.ReviewRepository
import com.ongo.signal.data.repository.review.ReviewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ReviewRepositoryModule {

    @Singleton
    @Binds
    fun bindReviewRepository(reviewRepositoryImpl: ReviewRepositoryImpl): ReviewRepository
}