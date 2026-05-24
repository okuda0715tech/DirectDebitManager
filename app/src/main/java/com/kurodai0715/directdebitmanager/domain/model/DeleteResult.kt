package com.kurodai0715.directdebitmanager.domain.model

sealed interface DeleteResult {
    data object Succeeded : DeleteResult
    data object Failed : DeleteResult
}