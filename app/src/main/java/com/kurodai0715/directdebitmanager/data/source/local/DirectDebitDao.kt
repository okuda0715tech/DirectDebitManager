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
    suspend fun upsertDestination(destination: LocalTransferItem)

    /**
     * 口座振替情報テーブルのレコードの削除 (主キーで削除) .
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteDestination(destination: LocalDestination): Int

    @Query("SELECT * FROM transfer_item WHERE isSourceItem = 1")
    fun observeSources(): Flow<List<LocalTransferItem>>

    /**
     * 振替元情報テーブルのレコードに対する Insert or Update.
     */
    @Upsert
    suspend fun upsertSource(source: LocalTransferItem)

    /**
     * 引数で指定した振替先情報テーブルのレコードの件数を取得.
     */
    @Query("SELECT COUNT(*) FROM transfer_item WHERE parentId = :parentId")
    suspend fun fetchNumOfDestination(parentId: Int): Int

    /**
     * 振替元情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteSource(source: LocalTransferItem): Int

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

    @Query("SELECT * FROM transfer_item")
    fun observeTransferItems(): Flow<List<LocalTransferItem>>

}