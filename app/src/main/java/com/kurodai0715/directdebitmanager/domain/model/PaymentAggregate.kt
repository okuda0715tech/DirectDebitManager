package com.kurodai0715.directdebitmanager.domain.model

data class PaymentAggregate(
    val payment: Payment,
    val payees: List<Payment.Persisted>,
)
