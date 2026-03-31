package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PaymentEditViewModel.kt"

data class PaymentEditUiState(
    val editMode: EditMode = EditMode.Add,
    val paymentName: String = "",
    val paymentNameMessage: Int? = null,
    val payerName: String = "",
    val payerNameMessage: Int? = null,
) {
    sealed interface EditMode {
        data object Add : EditMode
        data class Edit(val id: Int) : EditMode
    }
}

sealed class PaymentEditUiEvent {
    data class ShowSnackbar(val messageRes: Int) : PaymentEditUiEvent()
}

@HiltViewModel
class PaymentEditViewModel @Inject constructor(
    private val paymentQueryUseCase: PaymentQueryUseCase,
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

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<PaymentEditUiEvent>(Channel.BUFFERED)

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
            val item = paymentQueryUseCase.loadPaymentBy(paymentId)

            _uiState.update {
                it.copy(
                    editMode = PaymentEditUiState.EditMode.Edit(item.id),
                    paymentName = item.label
                )
            }
        }
    }

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

            when (result) {
                SaveResult.Succeeded -> {
                    _eventChannel.send(PaymentEditUiEvent.ShowSnackbar(R.string.common_save_successfully))
                }

                SaveResult.Failed ->
                    _eventChannel.send(PaymentEditUiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    private suspend fun savePayment(payment: Payment): SaveResult {
        return paymentCommandUseCase.savePayment(payment)
    }

    fun onPaymentSelected(id: Int) {
        // TODO 画面を更新する処理を実装する。

        Log.d(TAG, "onPaymentSelected.id = $id")
    }
}