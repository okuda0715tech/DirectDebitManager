package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.PaymentName

fun PaymentInfoEditUiState.toDomain(): Payment {
    return Payment(
        id = PaymentId(0),
        name = PaymentName(paymentName)
    )
}