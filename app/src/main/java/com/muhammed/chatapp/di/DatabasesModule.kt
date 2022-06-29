package com.muhammed.chatapp.di

import android.content.Context
import androidx.room.Room
import com.muhammed.chatapp.data.CacheDatabase
import com.muhammed.chatapp.data.implementation.network.FirestoreNetworkDatabaseImp
import com.muhammed.chatapp.data.NetworkDatabase
import com.muhammed.chatapp.data.implementation.local.ListConverter
import com.muhammed.chatapp.data.implementation.local.RoomDB
import com.muhammed.chatapp.domain.use_cases.SerializeEntityUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabasesModule {
    @Provides
    @Singleton
    fun provideNetworkDatabase(firestoreNetworkDatabaseImp: FirestoreNetworkDatabaseImp): NetworkDatabase =
        firestoreNetworkDatabaseImp

    @Provides
    @Singleton
    fun provideRoomDB(@ApplicationContext context: Context, serializeEntityUseCase: SerializeEntityUseCase): RoomDB =
        Room.databaseBuilder(context, RoomDB::class.java, "cache")
            .addTypeConverter(ListConverter(serializeEntityUseCase))
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCacheDatabase(roomDatabase: RoomDB): CacheDatabase = roomDatabase.messagesDao
}