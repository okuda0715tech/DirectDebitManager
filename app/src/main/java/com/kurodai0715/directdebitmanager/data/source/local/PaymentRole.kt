package com.kurodai0715.directdebitmanager.data.source.local

enum class PaymentRole(val value: Int) {
    Payer(1),
    Payee(2),
    Both(3);

    companion object {
        private val map = PaymentRole.entries.associateBy { it.value }

        fun fromValue(value: Int): PaymentRole =
            map[value] ?: error("Unknown PaymentType value: $value")
    }
}
