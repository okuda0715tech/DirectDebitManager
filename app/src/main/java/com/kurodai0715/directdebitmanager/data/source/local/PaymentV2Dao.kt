package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentV2Dao {

    @Query("SELECT * FROM payment WHERE id = :id")
    fun observePaymentBy(id: Int): Flow<PaymentEntity?>

    @Query("SELECT * FROM payment")
    fun observePayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payment WHERE parentId = :parentId")
    fun observePaymentsBy(parentId: Int): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payment WHERE id = :id")
    suspend fun loadItemBy(id: Int): PaymentEntity?

    @Query("SELECT * FROM payment WHERE parentId = :parentId")
    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntity>

    /**
     * 新規作成.
     *
     * @return 作成したレコードの id
     */
    @Insert
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("UPDATE payment SET parentId = 0 WHERE id IN (:ids)")
    suspend fun rootParentIds(ids: Set<Int>)

    /**
     * 指定した id のレコードを削除.
     *
     * @return 削除したレコードの件数
     */
    @Query("DELETE FROM payment WHERE id = :id")
    suspend fun deleteItem(id: Int): Int

}