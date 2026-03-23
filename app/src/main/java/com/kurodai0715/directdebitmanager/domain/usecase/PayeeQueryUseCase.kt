package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.domain.mapper.toPayee
import com.kurodai0715.directdebitmanager.domain.model.Payee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PayeeQueryUseCase @Inject constructor(
    private val repo: DirectDebitDefaultRepository
) {

    fun loadPayees(): Flow<List<Payee>> {
        return repo.observePayees().map { list ->
            list.map { it.toPayee() }
        }
    }

    suspend fun loadPayeeBy(payeeId: Int): Payee {
        return repo.loadItem(payeeId).toPayee()
    }

}