package com.kurodai0715.directdebitmanager.domain.usecase

import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoadSourcesUseCase @Inject constructor(
    private val repo: DirectDebitDefaultRepository
) {

    fun loadSources(): Flow<List<TransferItemEntity>> {
        return repo.loadSourcesStream()
    }

    fun loadSourceLabelsById(): Flow<Map<Int, String>> {
        return loadSources()
            .map { list ->
                list.associate { it.id to it.label }
            }
    }
}