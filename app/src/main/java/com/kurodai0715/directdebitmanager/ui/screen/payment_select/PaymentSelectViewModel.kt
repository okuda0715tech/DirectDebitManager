package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PaymentSelectUiState(
    val payments: List<PaymentSelectUiModel> = emptyList(),
)

data class PaymentSelectUiModel(
    val name: String,
    val selected: Boolean,
)

class PaymentSelectViewModel @Inject constructor(

) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(PaymentSelectUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<PaymentSelectUiState> = _uiState.asStateFlow()

    fun onClickItem(payment: PaymentSelectUiModel) {
        TODO()
    }
}