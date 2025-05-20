package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import com.kurodai0715.directdebitmanager.data.source.TransSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
)


@HiltViewModel
class EditDirectDebitViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(EditDirectDebitUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<EditDirectDebitUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // 振替元情報を取得し、 UI 画面状態に反映する。
            try {
                val sources = directDebitDefRepo.fetchTransSource()

                _uiState.update {
                    it.copy(
                        sources = sources
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _uiState.update {
                    it.copy(
                        userMessage = R.string.fetch_error
                    )
                }
            }
        }
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

    fun updateDirectDebit(directDebit: DirectDebit) {
        _uiState.update {
            it.copy(
                id = directDebit.id,
                transferDest = directDebit.destination,
                transferSource = directDebit.source,
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