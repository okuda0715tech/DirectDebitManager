package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectDebitDao {

    @Query("SELECT * FROM direct_debit")
    fun observeDirectDebit(): Flow<List<LocalDirectDebit>>

    /**
     * 口座振替情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsert(directDebit: LocalDirectDebit)

    /**
     * 口座振替情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun delete(directDebit: LocalDirectDebit): Int

}