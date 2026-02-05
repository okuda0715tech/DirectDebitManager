/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

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
    suspend fun upsertTransferItem(destination: TransferItemEntity)

    /**
     * 振替元として登録されているデータを全件取得.
     */
    @Query("SELECT * FROM transfer_item WHERE isSourceItem = 1")
    fun observeSources(): Flow<List<TransferItemEntity>>

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
    fun observeTransferItems(): Flow<List<TransferItemEntity>>

    /**
     * 振替先の ID をキーとして振替先情報を取得し、その振替元の [TransferItemEntity.label] も同時に取得する.
     */
    @Query(
        "SELECT dest.id, dest.label, dest.isSourceItem, dest.typeCode, dest.parentId, source.label AS source_name " +
                "FROM transfer_item AS dest " +
                "INNER JOIN transfer_item AS source " +
                "ON dest.parentId = source.id " +
                "WHERE dest.id = :id"
    )
    suspend fun getTransferInfo(id: Int): TransferInfoLocal

    /**
     * 指定した id のレコードを取得.
     */
    @Query("SELECT * FROM transfer_item WHERE id = :id")
    suspend fun getItem(id: Int): TransferItemEntity

}