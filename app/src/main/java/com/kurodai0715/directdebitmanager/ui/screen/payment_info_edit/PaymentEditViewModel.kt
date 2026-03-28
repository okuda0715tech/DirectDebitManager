package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentEditUiState(
    val paymentName: String = "",
    val paymentNameMessage: Int? = null,
    val payerName: String = "",
    val payerNameMessage: Int? = null,
)


@HiltViewModel
class PaymentEditViewModel @Inject constructor(
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(PaymentEditUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<PaymentEditUiState> = _uiState.asStateFlow()

    fun updatePaymentName(paymentName: String) {
        _uiState.update {
            it.copy(
                paymentName = paymentName
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            val payment = uiState.value.toDomain()

            val result = savePayment(payment)
        }
    }

    private suspend fun savePayment(payment: Payment): SaveResult {
        return paymentCommandUseCase.savePayment(payment)
    }

}