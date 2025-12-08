package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
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
     * 指定した id のレコードを削除.
     *
     * @return 削除したレコードの件数
     */
    @Query("DELETE FROM transfer_item WHERE id = :id")
    fun deleteItem(id: Int): Int

    /**
     * 振替元と振替先のデータを全件取得.
     */
    @Query("SELECT * FROM transfer_item")
    fun observeTransferItems(): Flow<List<LocalTransferItem>>

    /**
     * 振替元と振替先のデータを全件取得.
     */
    @Query(
        "SELECT dest.id, dest.label, dest.isSourceItem, dest.type, dest.parentId, source.label AS source_name " +
                "FROM transfer_item AS dest " +
                "INNER JOIN transfer_item AS source " +
                "ON dest.parentId = source.id " +
                "WHERE dest.id = :id"
    )
    suspend fun getTransferItem(id: Int): DestWithSourceLocal

    /**
     * 指定した id のレコードを取得.
     */
    @Query("SELECT * FROM transfer_item WHERE id = :id")
    suspend fun getItem(id: Int): LocalTransferItem

}