package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class EditDirectDebitUiState(
    val transferDest: String = "",
    val transferSource: String = "",
    val transferDate: String = "",
    val transferAmount: String = "",
)

@HiltViewModel
class EditDirectDebitViewModel @Inject constructor() : ViewModel() {

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

    fun updateDate(date: String) {
        _uiState.update {
            it.copy(transferDate = date)
        }
    }

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(transferAmount = amount)
        }
    }

    fun saveData(){
        // TODO
    }
}