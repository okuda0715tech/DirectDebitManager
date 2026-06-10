package com.kurodai0715.directdebitmanager.domain.mapper

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntity
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentEntity.toPayment(): Payment.Persisted {
    return Payment.Persisted(
        id = PaymentId.of(id),
        name = PaymentName.of(label),
        payerId = parentId?.let { PayerId.of(it) } ?: PayerId.NONE,
    )
}