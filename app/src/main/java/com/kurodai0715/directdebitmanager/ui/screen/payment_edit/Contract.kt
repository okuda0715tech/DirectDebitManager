package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.CreatePaymentResult
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val id: Int,
    val name: String,
)

sealed class UiEvent {
    data class ShowSnackbar(val messageRes: Int) : UiEvent()
}

@HiltViewModel
class ViewModel @Inject constructor(
    private val paymentCommandUseCase: PaymentCommandUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(id = 0, name = ""))

    val uiState: StateFlow<UiState> = _uiState

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onClickSave() {
        viewModelScope.launch {
            val payment = uiState.value.toDomain()

            val result = savePayment(payment)

            when (result) {
                CreatePaymentResult.Succeeded -> {
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_successfully))
                }

                CreatePaymentResult.Failed ->
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    private suspend fun savePayment(payment: Payment.InMemory): CreatePaymentResult {
        return paymentCommandUseCase.createPayment(payment)
    }
}