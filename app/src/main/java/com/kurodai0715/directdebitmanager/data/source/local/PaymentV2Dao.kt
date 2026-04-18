package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentV2Dao {

    @Query("SELECT * FROM payment_v2")
    fun observePayments(): Flow<List<PaymentEntityV2>>

    @Query("SELECT * FROM payment_v2 WHERE id = :id")
    suspend fun loadItemBy(id: Int): PaymentEntityV2?

    @Query("SELECT * FROM payment_v2 WHERE parentId = :parentId")
    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2>?

    /**
     * Insert or Update.
     */
    @Upsert
    suspend fun upsertPayment(payment: PaymentEntityV2)

}