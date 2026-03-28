package com.kurodai0715.directdebitmanager.data

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import kotlinx.coroutines.flow.Flow

interface PaymentV2Repository {

    fun loadPayments(): Flow<List<PaymentEntityV2>>

}