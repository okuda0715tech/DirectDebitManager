package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectDebitDao {

    /**
     * transfer_item テーブルに対する Insert or Update.
     */
    @Upsert
    suspend fun upsertTransferItem(destination: LocalTransferItem)

    /**
     * 口座振替情報テーブルのレコードの削除 (主キーで削除) .
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteDestination(destination: LocalTransferItem): Int

    /**
     * 振替元として登録されているデータを全件取得.
     */
    @Query("SELECT * FROM transfer_item WHERE isSourceItem = 1")
    fun observeSources(): Flow<List<LocalTransferItem>>

    /**
     * 引数で指定した parentId を振替元として使用している振替先の件数を取得.
     */
    @Query("SELECT COUNT(*) FROM transfer_item WHERE parentId = :parentId")
    suspend fun countDestinationsReferencing(parentId: Int): Int

    /**
     * 振替元情報テーブルのレコードの削除.
     *
     * @return 削除したレコードの件数
     */
    @Delete
    suspend fun deleteSource(source: LocalTransferItem): Int

    /**
     * 振替元と振替先のデータを全件取得.
     */
    @Query("SELECT * FROM transfer_item")
    fun observeTransferItems(): Flow<List<LocalTransferItem>>

}