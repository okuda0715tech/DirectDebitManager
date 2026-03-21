package com.kurodai0715.directdebitmanager.domain.model

data class Payee(
    val id: Int,
    val name: PayeeName,
)

@JvmInline
value class PayeeName(val value: String) {
    init {
        require(value.isNotBlank()) { "label is blank" }
    }
}