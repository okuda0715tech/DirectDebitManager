/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import javax.inject.Inject

class SourcesCommandUseCase @Inject constructor(
    private val repo: DirectDebitDefaultRepository
) {

    suspend fun saveDestination(
        destId: Int,
        label: String,
        isSourceItem: Boolean,
        parentId: Int
    ): Boolean {
        return when (destId) {
            0 ->
                repo.createDestination(
                    label = label,
                    isSourceItem = isSourceItem,
                    parentId = parentId,
                )

            else ->
                repo.updateDestination(
                    id = destId,
                    label = label,
                    isSourceItem = isSourceItem,
                    parentId = parentId,
                )
        }
    }
}