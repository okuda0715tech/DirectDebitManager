/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.PaymentDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.local.ChildWithParent
import com.kurodai0715.directdebitmanager.data.source.local.PaymentItemEntity
import com.kurodai0715.directdebitmanager.domain.model.DestInputType
import com.kurodai0715.directdebitmanager.domain.model.DestWithSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private fun ChildWithParent.toDestWithSource(): DestWithSource {
    return DestWithSource(
        destId = child.id,
        destName = child.label,
        destInputType = if (child.isSourceItem) DestInputType.SourceList else DestInputType.Keyboard,
        sourceId = child.parentId,
        sourceName = parent.label
    )
}

class PayerQueryUseCase @Inject constructor(
    private val repo: PaymentDefaultRepository
) {

    fun loadPayers(): Flow<List<PaymentItemEntity>> {
        return repo.observeByIsSource(isSource = true)
    }

    fun loadPayerLabelsById(): Flow<Map<Int, String>> {
        return loadPayers()
            .map { list ->
                list.associate { it.id to it.label }
            }
    }

    suspend fun loadDestWithSource(destId: Int): DestWithSource {
        return repo.loadItemWithParent(destId).toDestWithSource()
    }
}