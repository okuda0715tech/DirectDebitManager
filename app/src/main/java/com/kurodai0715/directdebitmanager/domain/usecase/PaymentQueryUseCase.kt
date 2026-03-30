package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymentQueryUseCase @Inject constructor(
    private val repo: PaymentV2Repository
) {

    fun loadPayments(): Flow<List<PaymentEntityV2>> {
        return repo.loadPayments()
    }

    suspend fun loadPaymentBy(paymentId: Int): PaymentEntityV2 {
        return repo.loadItemBy(paymentId)
    }

}