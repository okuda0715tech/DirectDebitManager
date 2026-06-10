package com.kurodai0715.directdebitmanager.data

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntity
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

    fun loadPaymentBy(id: Int): Flow<PaymentEntity?>

    fun loadPayments(): Flow<List<PaymentEntity>>

    fun loadPaymentsBy(parentId: Int): Flow<List<PaymentEntity>>

    suspend fun loadItemBy(id: Int): PaymentEntity?

    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntity>

    suspend fun requestSavePayment(id: Int?, name: String): RepositoryResult

    suspend fun saveRelations(
        paymentId: Int,
        payerId: PayerId,
        payeeIds: Set<PayeeId>
    ): RepositoryResult

    suspend fun requestDeletePayment(paymentId: PaymentId): RepositoryResult

    suspend fun requestDetachPayer(paymentId: Int): RepositoryResult

    suspend fun requestAddPayer(paymentId: Int, payerId: Int): RepositoryResult
}