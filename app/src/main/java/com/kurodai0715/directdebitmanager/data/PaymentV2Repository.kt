package com.kurodai0715.directdebitmanager.data

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import kotlinx.coroutines.flow.Flow

interface PaymentV2Repository {

    fun loadPayments(): Flow<List<PaymentEntityV2>>

    suspend fun loadItemBy(id: Int): PaymentEntityV2?

    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2>?

    suspend fun createPayment(label: String, parentId: Int? = null): Boolean

    suspend fun updatePayment(id: Int, label: String, parentId: Int? = null): Boolean

}