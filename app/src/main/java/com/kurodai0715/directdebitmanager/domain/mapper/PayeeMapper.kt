package com.kurodai0715.directdebitmanager.domain.mapper

import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.domain.model.Payee2
import com.kurodai0715.directdebitmanager.domain.model.PayeeName

fun TransferItemEntity.toPayee(): Payee2.Persisted {
    return Payee2.Persisted(
        id = id,
        name = PayeeName(label),
    )
}