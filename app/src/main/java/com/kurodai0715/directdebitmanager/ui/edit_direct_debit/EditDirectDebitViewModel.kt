package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditDirectDebitUiState(
    val id: Int = 0,
    val transferDest: String = "",
    val transferSource: String = "",
//    val transferDate: String = "",
//    val transferAmount: String = "",
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
            directDebitDefRepo.upsert(
                id = uiState.value.id,
                dest = uiState.value.transferDest,
                source = uiState.value.transferSource
            )
        }
    }
}