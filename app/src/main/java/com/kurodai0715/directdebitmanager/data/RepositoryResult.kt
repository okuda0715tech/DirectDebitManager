package com.kurodai0715.directdebitmanager.data

sealed interface RepositoryResult {
    data object Success : RepositoryResult
    data class Failure(val e: Exception) : RepositoryResult
}