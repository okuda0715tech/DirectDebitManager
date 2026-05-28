package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun UiState.getPaymentId(): Int {
    return when (this.payment.id) {
        is UiState.Payment.Id.Assigned -> this.payment.id.value
        is UiState.Payment.Id.Unassigned -> throw IllegalStateException("paymentId is Unassigned.")
    }
}

fun UiState.getPayerId(): PayerId {
    return when (this.payer) {
        is UiState.Payer.Unassigned -> PayerId.NONE
        is UiState.Payer.Assigned -> PayerId.of(this.payer.id)
    }
}

fun UiState.getPayeeIds(): Set<PayeeId> {
    return this.payees.map { PayeeId.of(it.id) }.toSet()
}

fun UiState.paymentToDomain(): Payment {
    val name = PaymentName.of(payment.name)

    val payerId = when (payer) {
        is UiState.Payer.Unassigned -> PayerId.NONE
        is UiState.Payer.Assigned -> PayerId.of(payer.id)
    }

    return when (val paymentId = payment.id) {
        is UiState.Payment.Id.Unassigned ->
            Payment.InMemory(
                name = name,
                payerId = payerId,
            )

        is UiState.Payment.Id.Assigned ->
            Payment.Persisted(
                id = PaymentId.of(paymentId.value),
                name = name,
                payerId = payerId,
            )
    }
}

fun UiState.paymentToDomain2(): Payment.Persisted {
    val name = PaymentName.of(payment.name)

    val payerId = when (payer) {
        is UiState.Payer.Unassigned -> PayerId.NONE
        is UiState.Payer.Assigned -> PayerId.of(payer.id)
    }

    return when (val paymentId = payment.id) {
        is UiState.Payment.Id.Unassigned ->
            throw IllegalStateException("paymentId is Unassigned.")

        is UiState.Payment.Id.Assigned ->
            Payment.Persisted(
                id = PaymentId.of(paymentId.value),
                name = name,
                payerId = payerId,
            )
    }
}

fun UiState.Payee.toDomain(): Payment.Persisted {
    return Payment.Persisted(
        id = PaymentId.of(id),
        name = PaymentName.of(name),
    )
}

fun List<UiState.Payee>.toDomain(): List<Payment.Persisted> {
    return map { it.toDomain() }
}

fun Payment.Persisted.toUiPayee(): UiState.Payee {
    return UiState.Payee(
        id = id.value,
        name = name.value,
    )
}

fun List<Payment.Persisted>.toUiPayees(): List<UiState.Payee> {
    return map { it.toUiPayee() }
}