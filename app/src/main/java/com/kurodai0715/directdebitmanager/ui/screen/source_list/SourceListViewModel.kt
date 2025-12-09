/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.source_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.data.source.toSourceUiModel
import com.kurodai0715.directdebitmanager.ui.screen.destination_edit.SourceUiModel
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SourceListUiState(
    val items: List<SourceUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    private val _sourcesAsync = directDebitDefRepo.loadSourcesStream2()
        .map { Async.Success(it) }
        .catch<Async<List<LocalTransferItem>>> { emit(Async.Error(R.string.load_error)) }

    val uiState: StateFlow<SourceListUiState> = _sourcesAsync.map { transSourcesAsync ->
        when (transSourcesAsync) {
            is Async.Loading -> {
                SourceListUiState(isLoading = true)
            }

            is Async.Error -> {
                SourceListUiState(userMessage = transSourcesAsync.errorMessage)
            }

            is Async.Success -> {
                SourceListUiState(
                    items = transSourcesAsync.data.map { it.toSourceUiModel() },
                    isLoading = false,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = SourceListUiState(isLoading = true)
    )

}