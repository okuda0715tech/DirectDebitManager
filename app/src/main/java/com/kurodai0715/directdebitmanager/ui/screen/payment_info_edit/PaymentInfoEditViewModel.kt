package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PaymentInfoEditUiState(
    val paymentName: String = "",
    val paymentNameErrorMessage: Int? = null,
)


@HiltViewModel
class PaymentInfoEditViewModel @Inject constructor(

) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(PaymentInfoEditUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<PaymentInfoEditUiState> = _uiState.asStateFlow()

    fun updatePaymentName(paymentName: String) {
        _uiState.update {
            it.copy(
                paymentName = paymentName
            )
        }
    }
}