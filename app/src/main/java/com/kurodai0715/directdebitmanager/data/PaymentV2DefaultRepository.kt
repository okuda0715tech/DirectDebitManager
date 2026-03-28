package com.kurodai0715.directdebitmanager.data

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.data.source.local.PaymentV2Dao
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymentV2DefaultRepository @Inject constructor(
    private val localDataSource: PaymentV2Dao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PaymentV2Repository {

    fun loadPayments(): Flow<List<PaymentEntityV2>> {
        return localDataSource.observePayments()
    }

}
