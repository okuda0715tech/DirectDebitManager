package com.kurodai0715.directdebitmanager.domain.model

sealed interface Payment {
    val name: PaymentName
    val payerId: PayerId

    data class Persisted(
        val id: PaymentId,
        override val name: PaymentName,
        override val payerId: PayerId = PayerId.NONE,
    ) : Payment

    data class InMemory(
        override val name: PaymentName,
        override val payerId: PayerId = PayerId.NONE,
    ) : Payment
}

@JvmInline
value class PaymentId(val value: Int)

@JvmInline
value class PaymentName(val value: String) {
    init {
        require(value.isNotBlank()) { "PaymentName is blank" }
    }
}

@JvmInline
value class PayerId private constructor(val value: Int) {
    companion object {
        val NONE = PayerId(-1)

        fun of(value: Int): PayerId {
            require(value >= 0) { "PayerId must be >= 0" }
            return PayerId(value)
        }
    }

    val isNone: Boolean
        get() = this == NONE

    val isValid: Boolean
        get() = this != NONE

    val valueOrNull: Int?
        get() = if (this == NONE) null else value
}