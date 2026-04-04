package com.kurodai0715.directdebitmanager.domain.mapper

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentEntityV2.toPayment(): Payment.Persisted {
    return Payment.Persisted(
        id = PaymentId(id),
        name = PaymentName(label),
        payerId = parentId?.let { PayerId.of(it) } ?: PayerId.NONE,
    )
}