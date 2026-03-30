package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName
import com.kurodai0715.directdebitmanager.domain.model.PaymentV2

fun PaymentEditUiState.toDomain(): PaymentV2 {

    return when (val mode = editMode) {
        is PaymentEditUiState.EditMode.Add -> {
            PaymentV2.InMemory(
                name = PaymentName(paymentName)
            )
        }

        is PaymentEditUiState.EditMode.Edit -> {
            PaymentV2.Persisted(
                id = PaymentId(mode.id),
                name = PaymentName(paymentName)
            )
        }
    }
}