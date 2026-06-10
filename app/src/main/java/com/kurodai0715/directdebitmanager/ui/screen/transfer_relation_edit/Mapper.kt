package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntity

fun PaymentEntity.toUiPayee(): UiState.Payee {
    return UiState.Payee(
        id = id,
        name = label,
    )
}

fun List<PaymentEntity>.toUiPayeesV2(): List<UiState.Payee> {
    return map { it.toUiPayee() }
}

fun PaymentEntity.toUiPayment(): UiState.Payment {
    return UiState.Payment(
        name = label,
    )
}
