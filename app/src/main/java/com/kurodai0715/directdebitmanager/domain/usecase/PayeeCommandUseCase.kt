package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.domain.model.Payee
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import javax.inject.Inject

class PayeeCommandUseCase @Inject constructor(
    private val repo: DirectDebitDefaultRepository
) {
    suspend fun savePayee(
        payee: Payee
    ): SaveResult {

        val result = repo.createPayee(
            label = payee.name,
        )

        return when (result) {
            true -> SaveResult.Succeeded
            false -> SaveResult.Failed
        }

    }
}