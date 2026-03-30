package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentEditUiState.toDomain(): Payment {

    return when (val mode = editMode) {
        is PaymentEditUiState.EditMode.Add -> {
            Payment(
                id = PaymentId(0),
                name = PaymentName(paymentName)
            )
        }

        is PaymentEditUiState.EditMode.Edit -> {
            Payment(
                id = PaymentId(mode.id),
                name = PaymentName(paymentName)
            )
        }
    }
}