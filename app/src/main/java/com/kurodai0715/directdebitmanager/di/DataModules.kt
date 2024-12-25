package com.kurodai0715.directdebitmanager.di

import android.content.Context
import androidx.room.Room
import com.kurodai0715.directdebitmanager.data.source.local.AppDatabase
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDirectDebitDao(database: AppDatabase): DirectDebitDao = database.directDebitDao()
}
