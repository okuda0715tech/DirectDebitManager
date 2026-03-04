/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.domain.model.Destination
import javax.inject.Inject

sealed interface SaveResult {
    data object Created : SaveResult
    data object Updated : SaveResult
    data object Failed : SaveResult
}

class SourcesCommandUseCase @Inject constructor(
    private val repo: DirectDebitDefaultRepository
) {

    suspend fun saveDestination(
        destination: Destination
    ): SaveResult {
        return when (destination) {
            is Destination.New -> {
                val result = repo.createDestination(
                    label = destination.label,
                    isSourceItem = destination.isSourceItem,
                    parentId = destination.parentId,
                )
                when (result) {
                    true -> SaveResult.Created
                    false -> SaveResult.Failed
                }
            }

            is Destination.Existing -> {
                val result = repo.updateDestination(
                    id = destination.id,
                    label = destination.label,
                    isSourceItem = destination.isSourceItem,
                    parentId = destination.parentId,
                )
                when (result) {
                    true -> SaveResult.Updated
                    false -> SaveResult.Failed
                }
            }
        }
    }
}
