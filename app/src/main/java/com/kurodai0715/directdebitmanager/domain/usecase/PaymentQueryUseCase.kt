package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.mapper.toPayment
import com.kurodai0715.directdebitmanager.domain.model.Payment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymentQueryUseCase @Inject constructor(
    private val repo: PaymentV2Repository
) {

    fun loadPaymentByV2(id: Int): Flow<PaymentEntityV2?> {
        return repo.loadPaymentBy(id)
    }

    fun loadPayments(): Flow<List<PaymentEntityV2>> {
        return repo.loadPayments()
    }

    suspend fun loadPaymentBy(paymentId: Int): Payment.Persisted? {
        return repo.loadItemBy(paymentId)?.toPayment()
    }

    suspend fun loadPaymentNameBy(paymentId: Int): String {
        return loadPaymentBy(paymentId)?.name?.value.orEmpty()
    }

    suspend fun loadPayeesBy(parentId: Int): List<Payment.Persisted> {
        return repo.loadChildItemsBy(parentId).map { it.toPayment() }
    }
}