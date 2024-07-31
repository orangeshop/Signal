package com.ongo.signal.di

import com.ongo.signal.data.repository.main.comment.CommentRepository
import com.ongo.signal.data.repository.main.comment.CommentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CommentRepositoryModule {

    @Singleton
    @Binds
    fun bindCommentRepository(commentRepositoryImpl: CommentRepositoryImpl): CommentRepository
}