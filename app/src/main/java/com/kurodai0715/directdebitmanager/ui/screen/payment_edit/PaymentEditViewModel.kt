package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PaymentEditViewModel.kt"

data class PaymentEditUiState(
    val payment: Payment = Payment(),
    val payer: Payer = Payer(),
) {
    sealed interface EditMode {
        data object Add : EditMode
        data class Edit(val id: Int) : EditMode
    }

    data class Payment(
        val editMode: EditMode = EditMode.Add,
        val name: String = "",
        val messageRes: Int? = null,
    )

    data class Payer(
        val id: Int? = null,
        val name: String = "",
        val messageRes: Int? = null,
    )
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
     * ユーザーが支払情報編集画面に直接入力した値.
     */
    private val payment = MutableStateFlow(PaymentEditUiState.Payment())

    private val payer = MutableStateFlow(PaymentEditUiState.Payer())

    /**
     * UI で必要となる全ての状態.
     */
    val uiState: StateFlow<PaymentEditUiState> = combine(payment, payer)
    { payment, payer ->
        PaymentEditUiState(payment = payment, payer = payer)
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = PaymentEditUiState()
    )

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
            val loadedPayment = paymentQueryUseCase.loadPaymentBy(paymentId)

            payment.update {
                it.copy(
                    editMode = PaymentEditUiState.EditMode.Edit(loadedPayment.id),
                    name = loadedPayment.label
                )
            }

            loadedPayment.parentId?.let { loadPayer(it) }
        }
    }

    fun updatePaymentName(paymentName: String) {
        payment.update {
            it.copy(
                name = paymentName
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
        loadPayer(id)
    }

    private fun loadPayer(id: Int) {
        viewModelScope.launch {
            val payerName = paymentQueryUseCase.loadPayerNameBy(id)

            payer.update {
                PaymentEditUiState.Payer(
                    id = id,
                    name = payerName,
                )
            }
        }
    }
}