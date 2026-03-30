package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import javax.inject.Inject

class PaymentCommandUseCase @Inject constructor(
    private val repo: PaymentV2Repository
) {
    suspend fun savePayment(
        payment: Payment
    ): SaveResult {
        val result = when(payment.id.value) {
            0 -> {
                repo.createPayment(
                    label = payment.name.value,
                )
            }
            else -> {
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