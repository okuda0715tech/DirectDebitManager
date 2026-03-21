package com.kurodai0715.directdebitmanager.domain.model

sealed interface SaveResult {
    data object Succeeded : SaveResult
    data object Failed : SaveResult
}