package com.kurodai0715.directdebitmanager.domain.model

sealed interface Payment {
    val name: PaymentName
    val payerId: PayerId
    val payeeId: PayeeId

    data class Persisted(
        val id: PaymentId,
        override val name: PaymentName,
        override val payerId: PayerId = PayerId.NONE,
        override val payeeId: PayeeId = PayeeId.NONE,
    ) : Payment

    data class InMemory(
        override val name: PaymentName,
        override val payerId: PayerId = PayerId.NONE,
        override val payeeId: PayeeId = PayeeId.NONE,
    ) : Payment
}

@JvmInline
value class PaymentId private constructor(val value: Int) {
    companion object {
        fun of(value: Int): PaymentId {
            require(value >= 0) { "PaymentId must be >= 0" }
            return PaymentId(value)
        }
    }
}

@JvmInline
value class PaymentName private constructor(val value: String) {
    companion object {
        fun of(value: String): PaymentName {
            require(value.isNotBlank()) { "PaymentName must not be blank" }
            return PaymentName(value)
        }
    }
}

@JvmInline
value class PayerId private constructor(val value: Int) {
    companion object {
        val NONE = PayerId(0)

        fun of(value: Int): PayerId {
            require(value >= 0) { "PayerId must be >= 0" }
            return PayerId(value)
        }
    }

    val isNone: Boolean
        get() = this == NONE

    val isValid: Boolean
        get() = this != NONE

    val valueOrZero: Int?
        get() = if (this == NONE) 0 else value
}

@JvmInline
value class PayeeId private constructor(val value: Int) {
    companion object {
        val NONE = PayeeId(0)

        fun of(value: Int): PayeeId {
            require(value >= 0) { "PayeeId must be >= 0" }
            return PayeeId(value)
        }
    }

    val isNone: Boolean
        get() = this == NONE

    val isValid: Boolean
        get() = this != NONE

    val valueOrZero: Int?
        get() = if (this == NONE) 0 else value
}