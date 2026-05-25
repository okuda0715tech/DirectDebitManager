package com.kurodai0715.directdebitmanager.data

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentAggregate
import kotlinx.coroutines.flow.Flow

interface PaymentV2Repository {

    fun loadPayments(): Flow<List<PaymentEntityV2>>

    suspend fun loadItemBy(id: Int): PaymentEntityV2?

    suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2>

    suspend fun requestCreatePayment(payment: Payment.InMemory): RepositoryResult

    suspend fun savePayments(aggregate: PaymentAggregate): RepositoryResult

    suspend fun requestDeletePayment(payment: Payment.Persisted): RepositoryResult
}