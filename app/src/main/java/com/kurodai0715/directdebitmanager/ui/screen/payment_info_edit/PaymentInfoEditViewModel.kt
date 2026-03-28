package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.lifecycle.ViewModel
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PaymentInfoEditUiState(
    val paymentName: String = "",
    val paymentNameMessage: Int? = null,
    val payerName: String = "",
    val payerNameMessage: Int? = null,
)


@HiltViewModel
class PaymentInfoEditViewModel @Inject constructor(
    private val paymentCommandUseCase: PaymentCommandUseCase,
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