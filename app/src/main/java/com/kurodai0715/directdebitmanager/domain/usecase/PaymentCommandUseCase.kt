package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import com.kurodai0715.directdebitmanager.domain.model.PaymentV2
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import javax.inject.Inject

class PaymentCommandUseCase @Inject constructor(
    private val repo: PaymentV2Repository
) {
    suspend fun savePayment(
        payment: PaymentV2
    ): SaveResult {
        val result = when (payment) {
            is PaymentV2.InMemory -> {
                repo.createPayment(
                    label = payment.name.value,
                )
            }

            is PaymentV2.Persisted -> {
                repo.updatePayment(
                    id = payment.id.value,
                    label = payment.name.value,
                )
            }
        }

        return when (result) {
            true -> SaveResult.Succeeded
            false -> SaveResult.Failed
        }

    }
}