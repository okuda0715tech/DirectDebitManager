package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.DeleteResult
import com.kurodai0715.directdebitmanager.domain.model.Payment
import com.kurodai0715.directdebitmanager.domain.model.PaymentAggregate
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.navigation.NavContract
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
    val payer: Payer = Payer.Unassigned,
    val payees: List<Payee> = mutableListOf(),
    val dialog: Dialog? = null,
) {
    data class Payment(
        val id: Id = Id.Unassigned,
        val name: String = "",
        val messageRes: Int? = null,
    ) {
        sealed interface Id {
            data object Unassigned : Id
            data class Assigned(val value: Int) : Id
        }
    }

    sealed interface Payer {
        data object Unassigned : Payer
        data class Assigned(
            val id: Int,
            val name: String = "",
            val messageRes: Int? = null,
        ) : Payer
    }

    data class Payee(
        val id: Int,
        val name: String = "",
        val messageRes: Int? = null,
    )

    sealed interface Dialog {
        data class DeleteConfirm(
            val itemName: String,
        ) : Dialog
    }
}

sealed class PaymentEditUiEvent {
    data class ShowSnackbar(val messageRes: Int) : PaymentEditUiEvent()
    data object OnClickBack : PaymentEditUiEvent()
    data object OnClickPayer : PaymentEditUiEvent()
    data object OnClickAddPayer : PaymentEditUiEvent()
    data object OnClickAddPayee : PaymentEditUiEvent()
    data object OnDeleted : PaymentEditUiEvent()
}

@HiltViewModel
class TransferRelationEditViewModel @Inject constructor(
    private val paymentQueryUseCase: PaymentQueryUseCase,
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    /**
     * ユーザーが支払情報編集画面に直接入力した値.
     */
    private val payment = MutableStateFlow(PaymentEditUiState.Payment())

    private val payer: MutableStateFlow<PaymentEditUiState.Payer> =
        MutableStateFlow(PaymentEditUiState.Payer.Unassigned)

    private val payees = MutableStateFlow<List<PaymentEditUiState.Payee>>(emptyList())

    private val dialog = MutableStateFlow<PaymentEditUiState.Dialog?>(null)

    /**
     * UI で必要となる全ての状態.
     */
    val uiState: StateFlow<PaymentEditUiState> = combine(payment, payer, payees, dialog)
    { payment, payer, payees, dialog ->
        PaymentEditUiState(payment = payment, payer = payer, payees = payees, dialog = dialog)
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

            require(loadedPayment != null) { "loadedPayment is null." }

            payment.update {
                it.copy(
                    id = PaymentEditUiState.Payment.Id.Assigned(loadedPayment.id.value),
                    name = loadedPayment.name.value
                )
            }

            if (loadedPayment.payerId.isValid)
                addPayer(loadedPayment.payerId.value)

            loadPayees(loadedPayment.id.value)
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
            val aggregate = PaymentAggregate(
                payment = uiState.value.paymentToDomain(),
                payees = uiState.value.payees.toDomain(),
            )

            val result = savePayments(aggregate)

            when (result) {
                SaveResult.Succeeded -> {
                    _eventChannel.send(PaymentEditUiEvent.ShowSnackbar(R.string.common_save_successfully))
                }

                SaveResult.Failed ->
                    _eventChannel.send(PaymentEditUiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    private suspend fun savePayments(aggregate: PaymentAggregate): SaveResult {
        return paymentCommandUseCase.savePayments(aggregate)
    }

    fun onPaymentSelected(target: NavContract.SelectTarget, id: Int) {
        when (target) {
            NavContract.SelectTarget.Payer -> addPayer(id)
            NavContract.SelectTarget.Payee -> addPayee(id)
        }
    }

    private fun addPayer(id: Int) {
        viewModelScope.launch {
            val payerName = paymentQueryUseCase.loadPaymentNameBy(id)

            payer.update {
                PaymentEditUiState.Payer.Assigned(
                    id = id,
                    name = payerName,
                )
            }
        }
    }

    private fun addPayee(id: Int) {
        viewModelScope.launch {
            val payeeName = paymentQueryUseCase.loadPaymentNameBy(id)

            payees.update {
                it + PaymentEditUiState.Payee(
                    id = id,
                    name = payeeName,
                )
            }
        }
    }

    fun onClickDetachPayee(id: Int) {
        removePayee(id)
    }

    private fun removePayee(id: Int) {
        payees.update { current ->
            current.filter { it.id != id }
        }
    }

    private fun loadPayees(id: Int) {
        viewModelScope.launch {
            val domainPayees = paymentQueryUseCase.loadPayeesBy(id)

            val uiPayees = domainPayees.toUiPayees()

            payees.update { uiPayees }
        }
    }

    fun onClickBack() {
        viewModelScope.launch {
            _eventChannel.send(PaymentEditUiEvent.OnClickBack)
        }
    }

    fun onClickDetachPayer() {
        removePayer()
    }

    private fun removePayer() {
        payer.update { PaymentEditUiState.Payer.Unassigned }
    }

    fun onClickAddPayer() {
        viewModelScope.launch {
            _eventChannel.send(PaymentEditUiEvent.OnClickAddPayer)
        }
    }

    fun onClickAddPayee() {
        viewModelScope.launch {
            _eventChannel.send(PaymentEditUiEvent.OnClickAddPayee)
        }
    }

    fun onClickDeletePayment() {
        dialog.update {
            PaymentEditUiState.Dialog.DeleteConfirm(
                itemName = payment.value.name
            )
        }
    }

    fun dismissDialog() {
        dialog.update { null }
    }

    fun onClickDeleteExecution() {
        viewModelScope.launch {
            val result = deletePayment(uiState.value.paymentToDomain2())

            when (result) {
                DeleteResult.Succeeded -> {
                    _eventChannel.send(PaymentEditUiEvent.OnDeleted)
                }

                DeleteResult.Failed ->
                    TODO()
            }
        }
    }

    private suspend fun deletePayment(payment: Payment.Persisted): DeleteResult {
        return paymentCommandUseCase.deletePayment(payment)
    }
}