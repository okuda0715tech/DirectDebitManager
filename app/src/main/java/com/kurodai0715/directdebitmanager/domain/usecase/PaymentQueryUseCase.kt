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

    fun loadPayments(): Flow<List<PaymentEntityV2>> {
        return repo.loadPayments()
    }

    suspend fun loadPaymentBy(paymentId: Int): PaymentEntityV2? {
        return repo.loadItemBy(paymentId)
    }

    suspend fun loadPaymentByV2(paymentId: Int): Payment.Persisted? {
        return repo.loadItemBy(paymentId)?.toPayment()
    }

    suspend fun loadPayerNameBy(payerId: Int): String {
        return loadPaymentBy(payerId)?.label.orEmpty()
    }

    suspend fun loadPayerNameByV2(payerId: Int): String {
        return loadPaymentByV2(payerId)?.name?.value.orEmpty()
    }
}