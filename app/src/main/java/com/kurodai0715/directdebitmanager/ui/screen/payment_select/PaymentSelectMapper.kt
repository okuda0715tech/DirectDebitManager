package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2

fun PaymentEntityV2.toPaymentSelect(): PaymentSelectUiState.Success.Item {
    return PaymentSelectUiState.Success.Item(
        id = id,
        name = label,
    )
}

fun List<PaymentEntityV2>.toPaymentSelect(): List<PaymentSelectUiState.Success.Item> {
    return map { it.toPaymentSelect() }
}