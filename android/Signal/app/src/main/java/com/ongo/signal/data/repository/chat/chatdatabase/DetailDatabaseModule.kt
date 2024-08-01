package com.ongo.signal.data.repository.chat.chatdatabase

//import android.content.Context
//import androidx.room.Room
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object DetailDatabaseModule {
//    @Provides
//    @Singleton
//    fun provideChatDetailDatabase(@ApplicationContext appContext: Context): ChatDetailDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            ChatDetailDatabase::class.java,
//            "chat_detail_database"
//        ).build()
//    }
//
//    @Provides
//    fun provideChatDetailDao(database: ChatDetailDatabase): ChatDetailDao {
//        return database.chatDetailDao()
//    }
//}