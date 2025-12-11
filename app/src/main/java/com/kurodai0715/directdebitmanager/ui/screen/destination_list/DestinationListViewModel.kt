/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "DestinationListViewModel.kt"

data class DestinationListUiState(
    val tabType: TabType = TabType.ListView,
    val items: List<DestWithSourceUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
)

data class DestWithSourceUiModel(
    val destId: Int,
    val destName: String,
    val sourceId: Int,
    val sourceName: String
)

@HiltViewModel
class DestinationListViewModel @Inject constructor(
    directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(DestinationListUiState())

    private val _destinationAsync = directDebitDefRepo.loadTransferItemsStream()
        .map { Async.Success(it) }
        .catch<Async<List<TransferItemEntity>>> { e ->
            Log.e(TAG, "loadDestWithSourcesStream failed.", e)
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<DestinationListUiState> =
        combine(
            _destinationAsync,
            _uiState
        ) { directDebitAsync, uiState ->

            when (directDebitAsync) {
                is Async.Loading -> {
                    uiState.copy(isLoading = true)
                }

                is Async.Error -> {
                    uiState.copy(userMessage = directDebitAsync.errorMessage)
                }

                is Async.Success -> {
                    uiState.copy(
                        items = directDebitAsync.data.convertModel(),
                        isLoading = false,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DestinationListUiState(isLoading = true)
        )

    fun updateTabType(tabType: TabType) {
        _uiState.update {
            it.copy(tabType = tabType)
        }
    }

    fun snackbarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

}
