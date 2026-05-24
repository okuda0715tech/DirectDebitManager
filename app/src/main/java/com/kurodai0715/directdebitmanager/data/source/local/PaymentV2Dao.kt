package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentV2Dao {

    @Query("SELECT * FROM payment_v2")
    fun observePayments(): Flow<List<PaymentEntityV2>>

    @Query("SELECT * FROM payment_v2 WHERE id = :id")
    suspend fun loadItemBy(id: Int): PaymentEntityV2?

    @Query("SELECT * FROM payment_v2 WHERE parentId = :parentId")
    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2>

    @Insert
    suspend fun insertPayment(payment: PaymentEntityV2): Long

    @Update
    suspend fun updatePayment(payment: PaymentEntityV2)

    @Query("UPDATE payment_v2 SET parentId = 0 WHERE id IN (:ids)")
    suspend fun rootParentIds(ids: Set<Int>)

    /**
     * 指定した id のレコードを削除.
     *
     * @return 削除したレコードの件数
     */
    @Query("DELETE FROM payment_v2 WHERE id = :id")
    suspend fun deleteItem(id: Int): Int

}