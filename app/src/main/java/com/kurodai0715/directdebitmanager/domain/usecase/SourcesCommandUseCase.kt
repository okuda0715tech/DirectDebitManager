/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
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
        destId: Int,
        label: String,
        isSourceItem: Boolean,
        parentId: Int
    ): SaveResult {
        return when (destId) {
            0 -> {
                val result = repo.createDestination(
                    label = label,
                    isSourceItem = isSourceItem,
                    parentId = parentId,
                )
                when (result) {
                    true -> SaveResult.Created
                    false -> SaveResult.Failed
                }
            }

            else -> {
                val result = repo.updateDestination(
                    id = destId,
                    label = label,
                    isSourceItem = isSourceItem,
                    parentId = parentId,
                )
                when (result) {
                    true -> SaveResult.Updated
                    false -> SaveResult.Failed
                }
            }
        }
    }
}
