package com.kurodai0715.directdebitmanager.data

import android.util.Log
import androidx.room.withTransaction
import com.kurodai0715.directdebitmanager.data.source.local.AppDatabase
import com.kurodai0715.directdebitmanager.data.source.local.PaymentDao
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntity
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "PaymentV2DefaultRepository.kt"

class PaymentDefaultRepository @Inject constructor(
    private val db: AppDatabase,
    private val localDataSource: PaymentDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PaymentV2Repository {

    override fun loadPaymentBy(id: Int): Flow<PaymentEntity?> {
        return localDataSource.observePaymentBy(id)
    }

    override fun loadPayments(): Flow<List<PaymentEntity>> {
        return localDataSource.observePayments()
    }

    override fun loadPaymentsBy(parentId: Int): Flow<List<PaymentEntity>> {
        return localDataSource.observePaymentsBy(parentId)
    }

    override suspend fun loadItemBy(id: Int): PaymentEntity? {
        return localDataSource.loadItemBy(id)
    }

    override suspend fun loadChildItemsBy(parentId: Int): List<PaymentEntity> {
        return localDataSource.loadChildItemsBy(parentId)
    }

    private suspend fun loadChildIdsBy(parentId: Int): Set<Int> {
        return localDataSource.loadChildItemsBy(parentId).map { it.id }.toSet()
    }

    override suspend fun requestSavePayment(id: Int?, name: String): RepositoryResult {
        return withContext(ioDispatcher) {
            try {
                upsertPayment(id, name)
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

                    requestDetachPayees(paymentId, payeeIds)

                    updatePayeesParentId(payeeIds, paymentId)
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
    private suspend fun upsertPayment(id: Int?, name: String) =
        when (id) {
            null -> {
                createPayment(name)
            }

            else -> {
                updatePayment(id, name)
            }
        }

    /**
     * 支払先の parentId を更新する.
     */
    private suspend fun updatePayeesParentId(
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
     * 振替関係を解除したい支払先の parentId を 0 で更新する.
     */
    private suspend fun requestDetachPayees(
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
        name: String
    ): Int {
        return withContext(ioDispatcher) {
            localDataSource.insertPayment(
                PaymentEntity(label = name)
            ).toInt()
        }
    }

    private suspend fun updatePayment(
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

    override suspend fun requestDeletePayment(paymentId: PaymentId): RepositoryResult {

        return withContext(ioDispatcher) {
            try {
                db.withTransaction {
                    val paymentId = paymentId.value
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

    override suspend fun requestDetachPayer(paymentId: Int): RepositoryResult {
        return withContext(ioDispatcher) {
            try {
                updateParentId(paymentId, 0)
                RepositoryResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                RepositoryResult.Failure(e)
            }
        }
    }

    override suspend fun requestAddPayer(paymentId: Int, payerId: Int): RepositoryResult {
        return try {
            updateParentId(paymentId, payerId)
            RepositoryResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            RepositoryResult.Failure(e)
        }
    }
}
