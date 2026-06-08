package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.model.Payment

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
