package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentEditUiState.toDomain(): Payment {
    val name = PaymentName.of(payment.name)

    val payerId = payer.id // or 別管理
        ?.let { PayerId.of(it) }
        ?: PayerId.NONE

    return when (val mode = payment.editMode) {
        is PaymentEditUiState.EditMode.Add ->
            Payment.InMemory(
                name = name,
                payerId = payerId,
            )

        is PaymentEditUiState.EditMode.Edit ->
            Payment.Persisted(
                id = PaymentId.of(mode.id),
                name = name,
                payerId = payerId,
            )
    }
}