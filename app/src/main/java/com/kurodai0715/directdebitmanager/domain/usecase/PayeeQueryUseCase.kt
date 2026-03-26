package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentDefaultRepository
import com.kurodai0715.directdebitmanager.domain.mapper.toPayee
import com.kurodai0715.directdebitmanager.domain.model.Payee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PayeeQueryUseCase @Inject constructor(
    private val repo: PaymentDefaultRepository
) {

    fun loadPayees(): Flow<List<Payee.Persisted>> {
        return repo.observePayees().map { list ->
            list.map { it.toPayee() }
        }
    }

    suspend fun loadPayeeBy(payeeId: Int): Payee.Persisted {
        return repo.loadItem(payeeId).toPayee()
    }

}