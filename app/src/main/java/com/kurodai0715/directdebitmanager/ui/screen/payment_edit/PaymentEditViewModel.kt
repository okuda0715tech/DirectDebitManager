package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class PaymentEditUiState(
    val id: Int,
    val name: String,
)

@HiltViewModel
class PaymentEditViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentEditUiState(id = 0, name = ""))

    val uiState: StateFlow<PaymentEditUiState> = _uiState

}