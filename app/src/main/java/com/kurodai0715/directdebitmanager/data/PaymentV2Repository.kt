package com.kurodai0715.directdebitmanager.data

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import kotlinx.coroutines.flow.Flow

interface PaymentV2Repository {

    fun loadPaymentBy(id: Int): Flow<PaymentEntityV2?>

    fun loadPayments(): Flow<List<PaymentEntityV2>>

    suspend fun loadItemBy(id: Int): PaymentEntityV2?

    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2>

    suspend fun requestSavePayment(id: Int?, name: String): RepositoryResult

    suspend fun saveRelations(paymentId: Int, payerId: PayerId, payeeIds: Set<PayeeId>): RepositoryResult

    suspend fun requestDeletePayment(paymentId: PaymentId): RepositoryResult
}