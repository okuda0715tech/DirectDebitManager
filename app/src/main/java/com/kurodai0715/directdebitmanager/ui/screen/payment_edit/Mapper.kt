package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun UiState.toDomain(): Payment.InMemory {
    return Payment.InMemory(
        name = PaymentName.of(this.name),
        payerId = PayerId.NONE
    )
}