package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectDebitDao {

    @Query("SELECT * FROM destination")
    fun observeDirectDebit(): Flow<List<LocalDestination>>

    /**
     * 口座振替情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsert(directDebit: LocalDestination)

    /**
     * 口座振替情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun delete(directDebit: LocalDestination): Int

    @Query("SELECT * FROM transfer_source")
    fun observeTransSource(): Flow<List<LocalTransSource>>

    @Query("SELECT * FROM transfer_source")
    fun fetchTransSource(): List<LocalTransSource>

    /**
     * 振替元情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsert(transSource: LocalTransSource)

    /**
     * 振替元情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun delete(transSource: LocalTransSource): Int

}