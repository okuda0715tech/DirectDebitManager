package com.kurodai0715.directdebitmanager.ui.screen.destination_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.DestWithSource
import com.kurodai0715.directdebitmanager.data.source.TransferItem
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "DestinationListViewModel.kt"

data class DestinationListUiState(
    val items: List<DestWithSource> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class DestinationListViewModel @Inject constructor(
    directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _destinationAsync = directDebitDefRepo.fetchTransferItemsStream()
        .map { Async.Success(it) }
        .catch<Async<List<TransferItem>>> { e ->
            Log.e(TAG, "fetchDestWithSourcesStream failed.", e)
            emit(Async.Error(R.string.fetch_error))
        }

    val uiState: StateFlow<DestinationListUiState> =
        combine(_destinationAsync, _userMessage) { directDebitAsync, userMessage ->

            when (directDebitAsync) {
                is Async.Loading -> {
                    DestinationListUiState(isLoading = true)
                }

                is Async.Error -> {
                    DestinationListUiState(userMessage = directDebitAsync.errorMessage)
                }

                is Async.Success -> {
                    DestinationListUiState(
                        items = convertModel(directDebitAsync.data),
                        isLoading = false,
                        userMessage = userMessage,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DestinationListUiState(isLoading = true)
        )

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    fun convertModel(transferItems: List<TransferItem>): List<DestWithSource> {
        return transferItems.mapNotNull {
            if (it.sourceId != null) {
                DestWithSource(
                    destId = it.id,
                    destName = it.label,
                    sourceId = it.sourceId,
                    sourceName = transferItems.find { item -> item.id == it.sourceId }?.label ?: "",
                )
            } else {
                null
            }
        }
    }
}