package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentEditUiState(
    val id: Int,
    val name: String,
)

@HiltViewModel
class PaymentEditViewModel @Inject constructor(
    private val paymentCommandUseCase: PaymentCommandUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentEditUiState(id = 0, name = ""))

    val uiState: StateFlow<PaymentEditUiState> = _uiState

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onClickSave() {
        val payment = uiState.value.toDomain()

        savePayment(payment)
    }

    private fun savePayment(payment: Payment.InMemory) {
        viewModelScope.launch {
            paymentCommandUseCase.createPayment(payment)
        }
    }
}