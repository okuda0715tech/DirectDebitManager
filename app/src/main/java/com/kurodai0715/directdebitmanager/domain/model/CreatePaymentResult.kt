package com.kurodai0715.directdebitmanager.domain.model

sealed interface CreatePaymentResult {
    data object Succeeded : CreatePaymentResult
    data object Failed : CreatePaymentResult
}