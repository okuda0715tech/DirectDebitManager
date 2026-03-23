package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.domain.model.Payee2
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import javax.inject.Inject

class PayeeCommandUseCase @Inject constructor(
    private val repo: DirectDebitDefaultRepository
) {
    suspend fun savePayee(
        payee: Payee2
    ): SaveResult {

        // TODO 判定を型で実施するように変更する。
        val result = when (payee) {
            is Payee2.InMemory -> {
                repo.createPayee(
                    label = payee.name.value,
                )
            }
            is Payee2.Persisted -> {
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