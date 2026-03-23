package com.kurodai0715.directdebitmanager.domain.model

@JvmInline
value class PayeeName(val value: String) {
    init {
        require(value.isNotBlank()) { "label is blank" }
    }
}

sealed interface Payee{

    val name: PayeeName

    data class Persisted(
        override val name: PayeeName,
        val id: Int,
    ): Payee

    data class InMemory(
        override val name: PayeeName,
    ): Payee

}
