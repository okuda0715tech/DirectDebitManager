package com.kurodai0715.directdebitmanager.domain.model

sealed interface DetachPayerResult {
    data object Succeeded : DetachPayerResult
    data object Failed : DetachPayerResult
}