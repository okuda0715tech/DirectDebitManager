package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentEditUiState.paymentToDomain(): Payment {
    val name = PaymentName.of(payment.name)

    val payerId = payer.id // or 別管理
        ?.let { PayerId.of(it) }
        ?: PayerId.NONE

    return when (val paymentId = payment.id) {
        is PaymentEditUiState.Payment.Id.Unassigned ->
            Payment.InMemory(
                name = name,
                payerId = payerId,
            )

        is PaymentEditUiState.Payment.Id.Assigned ->
            Payment.Persisted(
                id = PaymentId.of(paymentId.value),
                name = name,
                payerId = payerId,
            )
    }
}

fun PaymentEditUiState.Payee.toDomain(): Payment.Persisted {
    return Payment.Persisted(
        id = PaymentId.of(id!!),
        name = PaymentName.of(name),
    )
}

fun List<PaymentEditUiState.Payee>.toDomain(): List<Payment.Persisted> {
    return map { it.toDomain() }
}

fun Payment.Persisted.toUiPayee(): PaymentEditUiState.Payee {
    return PaymentEditUiState.Payee(
        id = id.value,
        name = name.value,
    )
}

fun List<Payment.Persisted>.toUiPayees(): List<PaymentEditUiState.Payee> {
    return map { it.toUiPayee() }
}