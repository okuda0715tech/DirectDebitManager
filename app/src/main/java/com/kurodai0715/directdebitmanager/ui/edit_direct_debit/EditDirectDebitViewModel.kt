package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
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
    val transferSource: String = "",
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val userMessage: Int? = null,
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

    fun updateDest(dest: String) {
        _uiState.update {
            it.copy(transferDest = dest)
        }
    }

    fun updateSource(source: String) {
        _uiState.update {
            it.copy(transferSource = source)
        }
    }

    fun updateAll(directDebit: DirectDebit) {
        _uiState.update {
            it.copy(
                id = directDebit.id,
                transferDest = directDebit.destination,
                transferSource = directDebit.source,
            )
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
                it.copy(
                    userMessage = if (numOfDeleted > 0) {
                        R.string.common_delete_successfully
                    } else {
                        R.string.common_delete_failed
                    }
                )
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