package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
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
     * 口座振替情報テーブルのレコードの削除 (主キーで削除) .
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteDestination(destination: LocalDestination): Int

    /**
     * 口座振替情報テーブルのレコードの削除 (外部キーで削除) .
     *
     * @return 削除したレコードの件数
     */
    @Query("DELETE FROM destination WHERE sourceId = :sourceId")
    suspend fun deleteDestinationsBy(sourceId: Int): Int

    @Query("SELECT * FROM source")
    fun observeSources(): Flow<List<LocalSource>>

    /**
     * 振替元情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsertSource(source: LocalTransferItem)

    /**
     * 引数で指定した振替先情報テーブルのレコードの件数を取得.
     */
    @Query("SELECT COUNT(*) FROM destination WHERE sourceId = :sourceId")
    suspend fun fetchNumOfDestination(sourceId: Int): Int

    /**
     * 振替元情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteSource(source: LocalSource): Int

    /**
     * 振替先情報テーブルと振替元情報テーブルを結合して、データを取得.
     */
    @Query(
        "SELECT d.id destId, d.name destName, s.id sourceId, s.name sourceName " +
                "FROM destination d " +
                "INNER JOIN source s " +
                "ON d.sourceId = s.id"
    )
    fun observeDestWithSource(): Flow<List<LocalDestWithSource>>

    /**
     * 引数で指定した振替元を削除しつつ、その振替元を使用している振替先のレコードを削除.
     */
    @Transaction
    suspend fun deleteDestAndSourceBy(source: LocalSource): Pair<Int, Int> {
        val deletedDestCount = deleteDestinationsBy(source.id)
        val deletedSourceCount = deleteSource(source)
        return (deletedDestCount to deletedSourceCount)
    }
}