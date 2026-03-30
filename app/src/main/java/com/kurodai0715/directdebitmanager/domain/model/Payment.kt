package com.kurodai0715.directdebitmanager.domain.model

sealed interface Payment{
    val name: PaymentName

    data class Persisted(
        override val name: PaymentName,
        val id: PaymentId,
    ): Payment

    data class InMemory(
        override val name: PaymentName,
    ): Payment
}

@JvmInline
value class PaymentId(val value: Int)

@JvmInline
value class PaymentName(val value: String) {
    init {
        require(value.isNotBlank()) { "PaymentName is blank" }
    }
}