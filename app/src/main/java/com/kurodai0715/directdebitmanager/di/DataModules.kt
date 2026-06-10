/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.di

import android.content.Context
import androidx.room.Room
import com.kurodai0715.directdebitmanager.data.source.local.AppDatabase
import com.kurodai0715.directdebitmanager.data.source.local.PaymentDao
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
        )
            // リリースするまでの一時的な対応のため、
            // リリース後は .fallbackToDestructiveMigration(true) を削除する。
            // この処理の役割は、スキーマ変更時にマイグレーション処理が書かれていない場合に、
            // 古いテーブルを削除して、新しいテーブルを作成するためのものです。
            // .fallbackToDestructiveMigration(true)
            .build()
    }

    @Singleton
    @Provides
    fun providePaymentV2Dao(database: AppDatabase): PaymentDao = database.paymentDao()
}
