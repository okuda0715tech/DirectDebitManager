package com.kurodai0715.directdebitmanager.domain.model

sealed interface PaymentV2{
    val name: PaymentName

    data class Persisted(
        override val name: PaymentName,
        val id: PaymentId,
    ): PaymentV2

    data class InMemory(
        override val name: PaymentName,
    ): PaymentV2
}

@JvmInline
value class PaymentId(val value: Int)

@JvmInline
value class PaymentName(val value: String) {
    init {
        require(value.isNotBlank()) { "PaymentName is blank" }
    }
}