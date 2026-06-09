package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.mapper.toPayment
import com.kurodai0715.directdebitmanager.domain.model.Payment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    fun loadPaymentsBy(parentId: Int): Flow<List<PaymentEntityV2>> {
        return repo.loadPaymentsBy(parentId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadPayerBy(paymentId: Int): Flow<PaymentEntityV2?> {
        return repo.loadPaymentBy(paymentId)
            .map { it?.parentId }
            .distinctUntilChanged()
            .flatMapLatest { parentId ->
                if (parentId != 0 && parentId != null) {
                    repo.loadPaymentBy(parentId)
                } else {
                    flowOf(null)
                }
            }
    }

    suspend fun loadPaymentBy(paymentId: Int): Payment.Persisted? {
        return repo.loadItemBy(paymentId)?.toPayment()
    }
}