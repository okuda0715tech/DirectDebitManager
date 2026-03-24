/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.payer_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.SourceUiModel
import com.kurodai0715.directdebitmanager.domain.usecase.SourcesQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PayerListUiState(
    val items: List<SourceUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class PayerListViewModel @Inject constructor(
    sourcesQueryUseCase: SourcesQueryUseCase,
) : ViewModel() {

    private val _sourcesAsync = sourcesQueryUseCase.loadSources()
        .map { Async.Success(it.toSourceUiModels()) }
        .catch<Async<List<SourceUiModel>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<PayerListUiState> = _sourcesAsync.map { transSourcesAsync ->
        when (transSourcesAsync) {
            is Async.Loading -> {
                PayerListUiState(isLoading = true)
            }

            is Async.Error -> {
                PayerListUiState(userMessage = transSourcesAsync.errorMessage)
            }

            is Async.Success -> {
                PayerListUiState(
                    items = transSourcesAsync.data,
                    isLoading = false,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = PayerListUiState(isLoading = true)
    )

}