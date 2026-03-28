package com.kurodai0715.directdebitmanager.domain.model

data class Payment(
    val id: PaymentId,
    val name: PaymentName,
)

@JvmInline
value class PaymentId(val value: Int)

@JvmInline
value class PaymentName(val value: String) {
    init {
        require(value.isNotBlank()) { "PaymentName is blank" }
    }
}