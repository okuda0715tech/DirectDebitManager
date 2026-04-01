package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentEditUiState.Payment.toDomain(): Payment {

    return when (val mode = editMode) {
        is PaymentEditUiState.EditMode.Add -> {
            Payment.InMemory(
                name = PaymentName(paymentName)
            )
        }

        is PaymentEditUiState.EditMode.Edit -> {
            Payment.Persisted(
                id = PaymentId(mode.id),
                name = PaymentName(paymentName)
            )
        }
    }
}