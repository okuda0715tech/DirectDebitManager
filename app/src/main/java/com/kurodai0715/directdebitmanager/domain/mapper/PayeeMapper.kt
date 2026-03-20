package com.kurodai0715.directdebitmanager.domain.mapper

import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.domain.model.Payee

fun TransferItemEntity.toPayee(): Payee {
    return Payee(
        id = id,
        name = label,
    )
}