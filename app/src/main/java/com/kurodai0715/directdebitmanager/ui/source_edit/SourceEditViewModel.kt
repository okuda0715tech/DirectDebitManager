package com.kurodai0715.directdebitmanager.ui.source_edit

import androidx.lifecycle.ViewModel
import com.kurodai0715.directdebitmanager.ui.edit_direct_debit.EditDirectDebitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SourceEditUiState(
    val id: Int = 0,
    val source: String = "",
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false
)

@HiltViewModel
class SourceEditViewModel @Inject constructor(

): ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(SourceEditUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<SourceEditUiState> = _uiState.asStateFlow()

    fun updateSource(source: String) {
        _uiState.update {
            it.copy(source = source)
        }
    }

    fun updateDelConfDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showDelConfDialog = show)
        }
    }

}