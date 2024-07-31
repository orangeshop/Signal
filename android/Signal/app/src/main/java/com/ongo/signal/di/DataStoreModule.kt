package com.ongo.signal.di

import android.content.Context
import com.ongo.signal.config.DataStoreClass
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    
    @Singleton
    @Provides
    fun provideDataStoreClass(@ApplicationContext context: Context): DataStoreClass {
        return DataStoreClass(context)
    }
}