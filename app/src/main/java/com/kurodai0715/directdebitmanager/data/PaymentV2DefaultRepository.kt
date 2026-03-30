package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.data.source.local.PaymentV2Dao
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "PaymentV2DefaultRepository.kt"

class PaymentV2DefaultRepository @Inject constructor(
    private val localDataSource: PaymentV2Dao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PaymentV2Repository {

    override fun loadPayments(): Flow<List<PaymentEntityV2>> {
        return localDataSource.observePayments()
    }

    override suspend fun loadItemBy(id: Int): PaymentEntityV2 {
        return localDataSource.loadItemBy(id)
    }

    override suspend fun createPayment(label: String): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertPayment(
                    PaymentEntityV2(
                        label = label,
                    )
                )
                true
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                false
            }
            Log.d(TAG, "resultSuccess = $resultSuccess")
        }
        return resultSuccess
    }

    override suspend fun updatePayment(id: Int, label: String): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertPayment(
                    // TODO 既存パラメータへの影響を避けるために、新規インスタンス生成ではなく、
                    //  既存インスタンスを取得して、それをコピーする。
                    PaymentEntityV2(
                        id = id,
                        label = label,
                    )
                )
                true
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                false
            }
            Log.d(TAG, "resultSuccess = $resultSuccess")
        }
        return resultSuccess
    }
}
