package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentDefaultRepository
import com.kurodai0715.directdebitmanager.domain.model.Payee
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import javax.inject.Inject

class PayeeCommandUseCase @Inject constructor(
    private val repo: PaymentDefaultRepository
) {
    suspend fun savePayee(
        payee: Payee
    ): SaveResult {

        val result = when (payee) {
            is Payee.InMemory -> {
                repo.createPayee(
                    label = payee.name.value,
                )
            }
            is Payee.Persisted -> {
                repo.updatePayee(
                    payeeId = payee.id,
                    label = payee.name.value,
                )
            }
        }

        return when (result) {
            true -> SaveResult.Succeeded
            false -> SaveResult.Failed
        }

    }
}