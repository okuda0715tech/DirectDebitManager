/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Note exportSchema は、リリース前に true に変更してください。
 */

@Database(
    entities = [TransferItemEntity::class, PaymentItemEntity::class, PaymentEntityV2::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(TransferItemConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun directDebitDao(): DirectDebitDao

    abstract fun paymentDao(): PaymentDao

    abstract fun paymentV2Dao(): PaymentV2Dao

}
