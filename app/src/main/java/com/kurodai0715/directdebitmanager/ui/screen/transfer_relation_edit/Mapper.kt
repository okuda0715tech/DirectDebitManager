package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment

fun UiState.getPayerId(): PayerId {
    return when (this.payer) {
        is UiState.Payer.Unassigned -> PayerId.NONE
        is UiState.Payer.Assigned -> PayerId.of(this.payer.id)
    }
}

fun UiState.getPayerIdInt(): Int {
    return getPayerId().value
}

fun UiState.getPayeeIds(): Set<PayeeId> {
    return this.payees.map { PayeeId.of(it.id) }.toSet()
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

fun PaymentEntityV2.toUiPayee(): UiState.Payee {
    return UiState.Payee(
        id = id,
        name = label,
    )
}

fun List<PaymentEntityV2>.toUiPayeesV2(): List<UiState.Payee> {
    return map { it.toUiPayee() }
}

fun PaymentEntityV2.toUiPayment(): UiState.Payment {
    return UiState.Payment(
        name = label,
    )
}
