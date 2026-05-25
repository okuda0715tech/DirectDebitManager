package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.CreatePaymentResult
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val id: Id = Id.Unassigned,
    val name: String = "",
) {
    sealed interface Id {
        data object Unassigned : Id
        data class Assigned(val value: Int) : Id
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val messageRes: Int) : UiEvent()
}

@HiltViewModel
class ViewModel @Inject constructor(
    private val paymentQueryUseCase: PaymentQueryUseCase,
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()

    private var initialized = false

    fun initialize(paymentId: Int?) {
        if (initialized) return
        initialized = true

        paymentId?.let { loadPaymentBy(it) }
    }

    private fun loadPaymentBy(paymentId: Int) {
        viewModelScope.launch {
            val loadedPayment = paymentQueryUseCase.loadPaymentBy(paymentId)

            require(loadedPayment != null) { "loadedPayment is null." }

            _uiState.update {
                it.copy(
                    id = UiState.Id.Assigned(loadedPayment.id.value),
                    name = loadedPayment.name.value
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onClickSave() {
        viewModelScope.launch {
            val payment = uiState.value.toDomainPayment()

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