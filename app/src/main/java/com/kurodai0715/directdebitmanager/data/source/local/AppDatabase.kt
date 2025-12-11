/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Note exportSchema は、リリース前に true に変更してください。
 */

@Database(entities = [TransferItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun directDebitDao(): DirectDebitDao
}
