package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectDebitDao {

    @Query("SELECT * FROM direct_debit")
    fun observeDirectDebit(): Flow<List<LocalDirectDebit>>

    @Upsert
    suspend fun upsert(directDebit: LocalDirectDebit)

}