package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectDebitDao {

    @Query("SELECT * FROM direct_debit")
    fun observeDirectDebit(): Flow<List<LocalDirectDebit>>

    /**
     * Insert 文.
     *
     * Room の 「 ID の自動採番」 機能を使用するために、
     * Insert と Update を別の関数に分けています。
     */
    @Insert
    suspend fun insert(directDebitList: LocalDirectDebit)

    /**
     * Update 文.
     *
     * Room の 「 ID の自動採番」 機能を使用するために、
     * Insert と Update を別の関数に分けています。
     */
    @Update
    suspend fun update(directDebitList: LocalDirectDebit)

}