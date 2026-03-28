/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.di

import android.content.Context
import androidx.room.Room
import com.kurodai0715.directdebitmanager.data.source.local.AppDatabase
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
import com.kurodai0715.directdebitmanager.data.source.local.PaymentDao
import com.kurodai0715.directdebitmanager.data.source.local.PaymentV2Dao
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

    @Singleton
    @Provides
    fun providePaymentDao(database: AppDatabase): PaymentDao = database.paymentDao()

    @Singleton
    @Provides
    fun providePaymentV2Dao(database: AppDatabase): PaymentV2Dao = database.paymentV2Dao()
}
