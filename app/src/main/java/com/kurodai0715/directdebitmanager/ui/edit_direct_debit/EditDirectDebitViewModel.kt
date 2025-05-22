package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.data.source.TransSource
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
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "EditDirectDebitViewModel.kt"

data class EditDirectDebitUiState(
    val id: Int = 0,
    val transferDest: String = "",
    val sourceId: Int = 0,
    val transferSource: String = "",
    val sources: List<TransSource> = emptyList(),
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val userMessage: Int? = null,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val showSourceListDialog: Boolean = false,
    val editSourceListEventConsumed: Boolean = true,
    val isLoading: Boolean = false,
)


@HiltViewModel
class EditDirectDebitViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(EditDirectDebitUiState())

    private val _transSourcesAsync = directDebitDefRepo.fetchTransSourceStream()
        .map { Async.Success(it) }
        .catch<Async<List<TransSource>>> {
            Log.e(TAG, "Failed to read trans sources.", it)
            emit(Async.Error(R.string.fetch_error))
        }

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<EditDirectDebitUiState> =
        combine(_transSourcesAsync, _uiState) { transSourcesAsync, uiState ->
            when (transSourcesAsync) {
                is Async.Loading -> {
                    uiState.copy(isLoading = true)
                }

                is Async.Error -> {
                    uiState.copy(userMessage = transSourcesAsync.errorMessage)
                }

                is Async.Success -> {
                    uiState.copy(
                        transferSource = updateSourceString(
                            sourceId = uiState.sourceId,
                            sources = transSourcesAsync.data
                        ),
                        sources = transSourcesAsync.data,
                        isLoading = false,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = EditDirectDebitUiState(isLoading = true)
        )

    private fun updateSourceString(sourceId: Int, sources: List<TransSource>): String {
        for (source in sources) {
            if (source.id == sourceId) {
                return source.source
            }
        }
        return ""
    }

    fun updateDest(dest: String) {
        _uiState.update {
            it.copy(transferDest = dest)
        }
    }

    fun updateSource(sourceId: Int, source: String) {
        _uiState.update {
            it.copy(
                sourceId = sourceId,
                transferSource = source,
            )
        }
    }

    fun updateDirectDebit(destination: Destination) {
        _uiState.update {
            it.copy(
                id = destination.destId,
                transferDest = destination.destName,
                transferSource = destination.sourceName,
            )
        }
    }

    fun updateDelConfDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showDelConfDialog = show)
        }
    }

    fun updateSourceListDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showSourceListDialog = show)
        }
    }

    fun updateEditSourceListEventConsumed(value: Boolean) {
        _uiState.update {
            it.copy(editSourceListEventConsumed = value)
        }
    }

//    fun updateDate(date: String) {
//        _uiState.update {
//            it.copy(transferDate = date)
//        }
//    }
//
//    fun updateAmount(amount: String) {
//        _uiState.update {
//            it.copy(transferAmount = amount)
//        }
//    }

    fun saveData() {
        viewModelScope.launch {
            val resultSuccess = directDebitDefRepo.upsert(
                id = uiState.value.id,
                dest = uiState.value.transferDest,
                source = uiState.value.transferSource
            )

            _uiState.update {
                if (resultSuccess) {
                    // 新規作成 or 更新が成功した場合
                    if (uiState.value.id == 0) {
                        // 新規作成の場合
                        it.copy(
                            transferDest = "",
                            transferSource = "",
                            userMessage = R.string.common_save_successfully
                        )
                    } else {
                        // 更新の場合
                        it.copy(
                            userMessage = R.string.common_update_successfully
                        )
                    }
                } else {
                    // 新規作成 or 更新が失敗した場合
                    it.copy(
                        userMessage = R.string.common_save_failed
                    )
                }
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            val numOfDeleted = directDebitDefRepo.delete(
                id = uiState.value.id,
                dest = uiState.value.transferDest,
                source = uiState.value.transferSource
            )

            _uiState.update {
                if (numOfDeleted > 0) {
                    // 削除に成功した場合

                    it.copy(
                        showDelCompDialog = true,
                    )
                } else {
                    // 削除に失敗した場合

                    it.copy(
                        userMessage = R.string.common_delete_failed,
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                userMessage = null
            )
        }
    }
}