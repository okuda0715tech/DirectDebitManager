package com.kurodai0715.directdebitmanager.ui.screen.register_payee

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RegisterPayeeUiState(
    val id: Int = 0,
    val payeeName: String = "",
)

@HiltViewModel
class RegisterPayeeViewModel @Inject constructor() : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(RegisterPayeeUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<RegisterPayeeUiState> = _uiState.asStateFlow()

    fun updatePayeeName(payeeName: String) {
        _uiState.update {
            it.copy(payeeName = payeeName)
        }
    }
}