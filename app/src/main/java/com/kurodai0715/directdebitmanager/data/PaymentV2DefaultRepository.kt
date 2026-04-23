package com.kurodai0715.directdebitmanager.data

import android.util.Log
import androidx.room.withTransaction
import com.kurodai0715.directdebitmanager.data.source.local.AppDatabase
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.data.source.local.PaymentV2Dao
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentAggregate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "PaymentV2DefaultRepository.kt"

class PaymentV2DefaultRepository @Inject constructor(
    private val db: AppDatabase,
    private val localDataSource: PaymentV2Dao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PaymentV2Repository {

    override fun loadPayments(): Flow<List<PaymentEntityV2>> {
        return localDataSource.observePayments()
    }

    override suspend fun loadItemBy(id: Int): PaymentEntityV2? {
        return localDataSource.loadItemBy(id)
    }

    override suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2>? {
        return localDataSource.loadChildItemsBy(parentId)
    }

    override suspend fun savePayments(aggregate: PaymentAggregate): RepositoryResult {

        return withContext(ioDispatcher) {
            try {
                db.withTransaction {
                    val paymentId = upsertPayment(aggregate)

                    rootDetachedPayees(paymentId, aggregate)

                    updatePayeesParentId(aggregate, paymentId)
                }

                RepositoryResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                RepositoryResult.Failure(e)
            }
        }
    }

    /**
     * 支払情報を更新または新規作成する.
     */
    private suspend fun upsertPayment(aggregate: PaymentAggregate): Int =
        when (val payment = aggregate.payment) {
            is Payment.Persisted -> {
                updatePayment(payment)
                payment.id.value
            }

            is Payment.InMemory -> {
                createPayment(payment)
            }
        }

    /**
     * 支払先の parentId を更新する.
     */
    private suspend fun updatePayeesParentId(
        aggregate: PaymentAggregate,
        paymentId: Int
    ) {
        aggregate.payees.forEach {
            updatePayment(
                it.copy(
                    payerId = PayerId.of(paymentId),
                )
            )
        }
    }

    /**
     * リンクを解除された支払先の parentId を 0 で更新する.
     */
    private suspend fun rootDetachedPayees(
        paymentId: Int,
        aggregate: PaymentAggregate
    ) {
        val currentPayees = loadChildItemsBy(paymentId)
        val currentIds = currentPayees?.map { it.id }?.toSet()
        val newIds = aggregate.payees.map { it.id.value }.toSet()
        val detachedIds = currentIds?.minus(newIds)

        detachedIds?.let { localDataSource.rootParentIds(detachedIds) }
    }

    private suspend fun createPayment(
        payment: Payment.InMemory
    ): Int {
        return withContext(ioDispatcher) {
            localDataSource.insertPayment(
                PaymentEntityV2(
                    label = payment.name.value,
                    parentId = payment.payerId.value,
                )
            ).toInt()
        }
    }

    private suspend fun updatePayment(
        payment: Payment.Persisted
    ) {
        withContext(ioDispatcher) {
            val item = localDataSource.loadItemBy(payment.id.value)
                ?: throw IllegalStateException(
                    "item is not found whose id = ${payment.id.value}."
                )

            localDataSource.updatePayment(
                item.copy(
                    id = payment.id.value,
                    label = payment.name.value,
                    parentId = payment.payerId.valueOrZero,
                )
            )
        }
    }
}
