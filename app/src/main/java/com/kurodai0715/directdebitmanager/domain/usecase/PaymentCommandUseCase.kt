package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import com.kurodai0715.directdebitmanager.data.RepositoryResult
import com.kurodai0715.directdebitmanager.domain.model.CreatePaymentResult
import com.kurodai0715.directdebitmanager.domain.model.DeleteResult
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentAggregate
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import javax.inject.Inject

class PaymentCommandUseCase @Inject constructor(
    private val repo: PaymentV2Repository
) {
    suspend fun createPayment(payment: Payment.InMemory): CreatePaymentResult {
        val result = repo.requestCreatePayment(payment)

        return when (result) {
            is RepositoryResult.Success -> CreatePaymentResult.Succeeded
            is RepositoryResult.Failure -> CreatePaymentResult.Failed
        }
    }

    suspend fun savePayments(aggregate: PaymentAggregate): SaveResult {
        val result = repo.savePayments(aggregate)

        return when (result) {
            is RepositoryResult.Success -> SaveResult.Succeeded
            is RepositoryResult.Failure -> SaveResult.Failed
        }
    }

    suspend fun deletePayment(payment: Payment.Persisted): DeleteResult {
        val result = repo.requestDeletePayment(payment)

        return when (result) {
            is RepositoryResult.Success -> DeleteResult.Succeeded
            is RepositoryResult.Failure -> DeleteResult.Failed
        }
    }
}