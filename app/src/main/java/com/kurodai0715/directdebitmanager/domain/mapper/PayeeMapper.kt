package com.kurodai0715.directdebitmanager.domain.mapper

import com.kurodai0715.directdebitmanager.data.source.local.PaymentItemEntity
import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.domain.model.Payee
import com.kurodai0715.directdebitmanager.domain.model.PayeeName

fun PaymentItemEntity.toPayee(): Payee.Persisted {
    return Payee.Persisted(
        id = id,
        name = PayeeName(label),
    )
}