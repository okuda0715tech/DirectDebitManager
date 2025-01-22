package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Note exportSchema は、りりーす前に true に変更してください。
 */

@Database(entities = [LocalDirectDebit::class, LocalTransSource::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun directDebitDao(): DirectDebitDao
}
