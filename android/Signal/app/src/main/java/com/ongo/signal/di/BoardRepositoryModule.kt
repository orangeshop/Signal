package com.ongo.signal.di

import com.ongo.signal.data.repository.main.board.BoardRepository
import com.ongo.signal.data.repository.main.board.BoardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BoardRepositoryModule {

    @Singleton
    @Binds
    fun bindBoardRepository(
        boardRepositoryImpl: BoardRepositoryImpl
    ): BoardRepository
}