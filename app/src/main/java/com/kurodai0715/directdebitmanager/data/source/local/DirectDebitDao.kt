package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectDebitDao {

    /**
     * 口座振替情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsertDestination(destination: LocalDestination)

    /**
     * 口座振替情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteDestination(destination: LocalDestination): Int

    @Query("SELECT * FROM source")
    fun observeSources(): Flow<List<LocalSource>>

    /**
     * 振替元情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsertSource(source: LocalSource)

    /**
     * 振替元情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteSource(source: LocalSource): Int

    @Query("SELECT d.id destId, d.name destName, s.id sourceId, s.name sourceName " +
            "FROM destination d " +
            "INNER JOIN source s " +
            "ON d.sourceId = s.id")
    fun observeDestWithSource(): Flow<List<LocalDestWithSource>>
}