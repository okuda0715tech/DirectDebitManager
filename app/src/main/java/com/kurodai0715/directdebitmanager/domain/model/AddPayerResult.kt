package com.kurodai0715.directdebitmanager.domain.model

sealed interface AddPayerResult {
    data object Succeeded : AddPayerResult
    data object Failed : AddPayerResult
}