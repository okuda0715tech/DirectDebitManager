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
interface PaymentDao {

    /**
     * payment_item テーブルに対する Insert or Update.
     */
    @Upsert
    suspend fun upsertPaymentItem(destination: PaymentItemEntity)

    /**
     * isSourceItem 列が指定した値に一致するレコードを取得.
     */
    @Query("SELECT * FROM payment_item WHERE isSourceItem = :isSource")
    fun observeByIsSource(isSource: Boolean): Flow<List<PaymentItemEntity>>

    /**
     * 引数で指定した parentId を振替元として使用している振替先の件数を取得.
     */
    @Query("SELECT COUNT(*) FROM payment_item WHERE parentId = :parentId")
    suspend fun countDestinationsReferencing(parentId: Int): Int

    /**
     * 指定した id のレコードを削除.
     *
     * @return 削除したレコードの件数
     */
    @Query("DELETE FROM payment_item WHERE id = :id")
    fun deleteItem(id: Int): Int

    /**
     * 振替元と振替先のデータを全件取得.
     */
    @Query("SELECT * FROM payment_item")
    fun observePaymentItems(): Flow<List<PaymentItemEntity>>

    /**
     * 与えられた ID のデータとその親のデータを取得する.
     */
    @Query(
        "SELECT " +
                "dest.id AS child_id, " +
                "dest.label AS child_label, " +
                "dest.isSourceItem AS child_isSourceItem, " +
                "dest.typeCode AS child_typeCode, " +
                "dest.parentId AS child_parentId, " +
                "dest.role AS child_role, " +
                "source.id AS parent_id, " +
                "source.label AS parent_label, " +
                "source.isSourceItem AS parent_isSourceItem, " +
                "source.typeCode AS parent_typeCode, " +
                "source.parentId AS parent_parentId, " +
                "source.role AS parent_role " +
                "FROM payment_item AS dest " +
                "INNER JOIN payment_item AS source " +
                "ON dest.parentId = source.id " +
                "WHERE dest.id = :id"
    )
    suspend fun getItemWithParent(id: Int): ChildWithParent

    /**
     * 指定した id のレコードを取得.
     */
    @Query("SELECT * FROM payment_item WHERE id = :id")
    suspend fun getItem(id: Int): PaymentItemEntity

    /**
     * 指定した role のレコードを取得.
     */
    @Query("SELECT * FROM payment_item WHERE role IN (:roles)")
    fun observeByRoles(roles: List<PaymentRole>): Flow<List<PaymentItemEntity>>

}
