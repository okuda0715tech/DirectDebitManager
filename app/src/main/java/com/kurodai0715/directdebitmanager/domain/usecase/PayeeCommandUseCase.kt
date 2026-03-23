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

        // TODO 判定を型で実施するように変更する。
        val result = when (payee.id) {
            0 -> {
                repo.createPayee(
                    label = payee.name.value,
                )
            }
            else -> {
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