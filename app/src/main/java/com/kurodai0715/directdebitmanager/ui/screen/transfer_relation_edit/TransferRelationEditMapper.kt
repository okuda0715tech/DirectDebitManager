package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun TransferRelationEditUiState.paymentToDomain(): Payment {
    val name = PaymentName.of(payment.name)

    val payerId = when (payer) {
        is TransferRelationEditUiState.Payer.Unassigned -> PayerId.NONE
        is TransferRelationEditUiState.Payer.Assigned -> PayerId.of(payer.id)
    }

    return when (val paymentId = payment.id) {
        is TransferRelationEditUiState.Payment.Id.Unassigned ->
            Payment.InMemory(
                name = name,
                payerId = payerId,
            )

        is TransferRelationEditUiState.Payment.Id.Assigned ->
            Payment.Persisted(
                id = PaymentId.of(paymentId.value),
                name = name,
                payerId = payerId,
            )
    }
}

fun TransferRelationEditUiState.paymentToDomain2(): Payment.Persisted {
    val name = PaymentName.of(payment.name)

    val payerId = when (payer) {
        is TransferRelationEditUiState.Payer.Unassigned -> PayerId.NONE
        is TransferRelationEditUiState.Payer.Assigned -> PayerId.of(payer.id)
    }

    return when (val paymentId = payment.id) {
        is TransferRelationEditUiState.Payment.Id.Unassigned ->
            throw IllegalStateException("paymentId is Unassigned.")

        is TransferRelationEditUiState.Payment.Id.Assigned ->
            Payment.Persisted(
                id = PaymentId.of(paymentId.value),
                name = name,
                payerId = payerId,
            )
    }
}

fun TransferRelationEditUiState.Payee.toDomain(): Payment.Persisted {
    return Payment.Persisted(
        id = PaymentId.of(id),
        name = PaymentName.of(name),
    )
}

fun List<TransferRelationEditUiState.Payee>.toDomain(): List<Payment.Persisted> {
    return map { it.toDomain() }
}

fun Payment.Persisted.toUiPayee(): TransferRelationEditUiState.Payee {
    return TransferRelationEditUiState.Payee(
        id = id.value,
        name = name.value,
    )
}

fun List<Payment.Persisted>.toUiPayees(): List<TransferRelationEditUiState.Payee> {
    return map { it.toUiPayee() }
}