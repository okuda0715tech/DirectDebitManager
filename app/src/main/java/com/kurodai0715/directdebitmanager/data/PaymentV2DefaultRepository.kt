package com.kurodai0715.directdebitmanager.data

import android.util.Log
import androidx.room.withTransaction
import com.kurodai0715.directdebitmanager.data.source.local.AppDatabase
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.data.source.local.PaymentV2Dao
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
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

    override suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntityV2> {
        return localDataSource.loadChildItemsBy(parentId)
    }

    private suspend fun loadChildIdsBy(parentId: Int): Set<Int> {
        return localDataSource.loadChildItemsBy(parentId).map { it.id }.toSet()
    }

    override suspend fun requestSavePayment(id: Int?, name: String): RepositoryResult {
        return withContext(ioDispatcher) {
            try {
                upsertPaymentV2(id, name)
                RepositoryResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                RepositoryResult.Failure(e)
            }
        }
    }

    override suspend fun saveRelations(
        paymentId: Int,
        payerId: PayerId,
        payeeIds: Set<PayeeId>
    ): RepositoryResult {

        return withContext(ioDispatcher) {
            try {
                db.withTransaction {
                    updateParentId(paymentId, payerId.value)

                    requestDetachPayeesV2(paymentId, payeeIds)

                    updatePayeesParentIdV2(payeeIds, paymentId)
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
    private suspend fun upsertPayment(payment: Payment): Int =
        when (payment) {
            is Payment.Persisted -> {
                updatePayment(payment)
                payment.id.value
            }

            is Payment.InMemory -> {
                createPayment(payment)
            }
        }

    /**
     * 支払情報を更新または新規作成する.
     */
    private suspend fun upsertPaymentV2(id: Int?, name: String) =
        when (id) {
            null -> {
                createPaymentV2(name)
            }

            else -> {
                updatePaymentV2(id, name)
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
     * 支払先の parentId を更新する.
     */
    private suspend fun updatePayeesParentIdV2(
        payeeIds: Set<PayeeId>,
        paymentId: Int
    ) {
        payeeIds.map { it.value }.forEach {
            updateParentId(
                paymentId = it,
                parentId = paymentId
            )
        }
    }

    /**
     * リンクを解除された支払先の parentId を 0 で更新する.
     */
    private suspend fun requestDetachPayees(
        paymentId: Int,
        aggregate: PaymentAggregate
    ) {
        val currentIds = loadChildIdsBy(paymentId)
        val newIds = aggregate.payees.map { it.id.value }.toSet()

        detachPayees(currentIds - newIds)
    }

    /**
     * 振替関係を解除したい支払先の parentId を 0 で更新する.
     */
    private suspend fun requestDetachPayeesV2(
        paymentId: Int,
        payeeIds: Set<PayeeId>
    ) {
        val currentIds = loadChildIdsBy(paymentId)
        val newIds = payeeIds.map { it.value }.toSet()

        detachPayees(currentIds - newIds)
    }

    /**
     * 引数で指定された支払情報の parentId を 0 にする.
     *
     * @param payeeIds: 対象となる支払情報の id のコレクション
     */
    private suspend fun detachPayees(payeeIds: Set<Int>) {
        // 空なら何もしない。
        if (payeeIds.isEmpty()) return
        // 空でない場合は実行する。
        localDataSource.rootParentIds(payeeIds)
    }

    /**
     * 新規作成.
     *
     * @return 作成したレコードの id
     */
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

    /**
     * 新規作成.
     *
     * @return 作成したレコードの id
     */
    private suspend fun createPaymentV2(
        name: String
    ): Int {
        return withContext(ioDispatcher) {
            localDataSource.insertPayment(
                PaymentEntityV2(label = name)
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

    private suspend fun updatePaymentV2(
        id: Int, name: String
    ) {
        withContext(ioDispatcher) {
            val item = localDataSource.loadItemBy(id)
                ?: throw IllegalStateException(
                    "item is not found whose id = $id."
                )

            localDataSource.updatePayment(
                item.copy(
                    id = id,
                    label = name,
                )
            )
        }
    }

    private suspend fun updateParentId(
        paymentId: Int, parentId: Int
    ) {
        withContext(ioDispatcher) {
            val item = localDataSource.loadItemBy(paymentId)
                ?: throw IllegalStateException(
                    "item is not found whose id = $paymentId."
                )

            localDataSource.updatePayment(
                item.copy(
                    id = paymentId,
                    parentId = parentId,
                )
            )
        }
    }

    override suspend fun requestDeletePayment(payment: Payment.Persisted): RepositoryResult {

        return withContext(ioDispatcher) {
            try {
                db.withTransaction {
                    val paymentId = payment.id.value
                    deletePayment(paymentId)

                    val childIds = loadChildIdsBy(paymentId)

                    detachPayees(childIds)
                }

                RepositoryResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                RepositoryResult.Failure(e)
            }
        }
    }

    /**
     * 支払情報を削除する.
     */
    private suspend fun deletePayment(
        paymentId: Int
    ) {
        withContext(ioDispatcher) {
            val item = localDataSource.loadItemBy(paymentId)
                ?: throw IllegalStateException(
                    "item is not found whose id = ${paymentId}."
                )

            localDataSource.deleteItem(item.id)
        }
    }
}
