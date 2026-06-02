package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.DeleteResult
import com.kurodai0715.directdebitmanager.domain.model.DetachPayerResult
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.PaymentId
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.navigation.NavContract
import com.kurodai0715.directdebitmanager.ui.navigation.TransferRelationEditGraph
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PaymentEditViewModel.kt"

data class UiState(
    val payment: Payment = Payment(),
    val payer: Payer = Payer.Unassigned,
    val payees: List<Payee> = mutableListOf(),
    val dialog: Dialog = Dialog.None,
) {
    data class Payment(
        val name: String = "",
        val messageRes: Int? = null,
    )

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
        data object None : Dialog

        data class DeleteConfirm(
            val itemName: String,
        ) : Dialog

        data class DetachPayerConfirm(
            val paymentName: String,
            val payerName: String,
        ) : Dialog

        sealed interface Action {
            data object Dismiss : Action
            data object No : Action
            data object DeleteYes : Action
            data object DetachPayerYes : Action
        }
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val messageRes: Int) : UiEvent()
    data class OpenPaymentEdit(val paymentId: Int) : UiEvent()
    data object OnClickAddPayer : UiEvent()
    data object OnClickAddPayee : UiEvent()
    data object OnDeleted : UiEvent()
}

@HiltViewModel
class TransferRelationEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paymentQueryUseCase: PaymentQueryUseCase,
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    // 【技術的メモ】
    // AppNavGraph.kt 内で TransferRelationEditViewModel を生成する際に、
    // hiltViewModel() の引数に TransferRelationEditGraph の backStackEntry を渡しているため、
    // TransferRelationEditGraph のパラメータがこの TransferRelationEditViewModel の
    // SavedStateHandle に渡される。
    private val paymentId: Int = savedStateHandle
        .toRoute<TransferRelationEditGraph>()
        .paymentId

    private val payment = paymentQueryUseCase.loadPaymentByV2(paymentId)
        .map {
            it?.toUiPayment() ?: UiState.Payment()
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = UiState.Payment()
        )

    private val payer: MutableStateFlow<UiState.Payer> =
        MutableStateFlow(UiState.Payer.Unassigned)

    private val payees = MutableStateFlow<List<UiState.Payee>>(emptyList())

    private val dialog = MutableStateFlow<UiState.Dialog>(UiState.Dialog.None)

    /**
     * UI で必要となる全ての状態.
     */
    val uiState: StateFlow<UiState> = combine(payment, payer, payees, dialog)
    { payment, payer, payees, dialog ->
        UiState(payment = payment, payer = payer, payees = payees, dialog = dialog)
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = UiState()
    )

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        loadPaymentBy(paymentId)
    }

    private fun loadPaymentBy(paymentId: Int) {
        Log.d(TAG, "loadPaymentBy: paymentId=$paymentId")

        viewModelScope.launch {
            val loadedPayment = paymentQueryUseCase.loadPaymentBy(paymentId)

            require(loadedPayment != null) { "loadedPayment is null." }

            if (loadedPayment.payerId.isValid)
                addPayer(loadedPayment.payerId.value)

            loadPayees(loadedPayment.id.value)
        }
    }

    fun save() {
        viewModelScope.launch {
            val result = saveRelations(
                paymentId = paymentId,
                payerId = uiState.value.getPayerId(),
                payeeIds = uiState.value.getPayeeIds(),
            )

            when (result) {
                SaveResult.Succeeded -> {
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_successfully))
                }

                SaveResult.Failed ->
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    private suspend fun saveRelations(
        paymentId: Int,
        payerId: PayerId,
        payeeIds: Set<PayeeId>
    ): SaveResult {
        return paymentCommandUseCase.saveRelations(paymentId, payerId, payeeIds)
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
                UiState.Payer.Assigned(
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
                it + UiState.Payee(
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

    fun onClickDetachPayerIcon(payerName: String) {
        dialog.update {
            UiState.Dialog.DetachPayerConfirm(
                paymentName = payment.value.name,
                payerName = payerName,
            )
        }
    }

    fun onClickAddPayer() {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.OnClickAddPayer)
        }
    }

    fun onClickAddPayee() {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.OnClickAddPayee)
        }
    }

    fun onClickDeletePayment() {
        dialog.update {
            UiState.Dialog.DeleteConfirm(
                itemName = payment.value.name
            )
        }
    }

    fun dismissDialog() {
        dialog.update { UiState.Dialog.None }
    }

    fun onClickDeleteExecution() {
        viewModelScope.launch {
            val result = deletePayment(PaymentId.of(paymentId))

            when (result) {
                DeleteResult.Succeeded -> {
                    _eventChannel.send(UiEvent.OnDeleted)
                }

                DeleteResult.Failed ->
                    TODO()
            }
        }
    }

    private suspend fun deletePayment(paymentId: PaymentId): DeleteResult {
        return paymentCommandUseCase.deletePayment(paymentId)
    }

    fun onClickPayment() {
        openPaymentEdit(paymentId)
    }

    private fun openPaymentEdit(id: Int) {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.OpenPaymentEdit(id))
        }
    }

    fun onDialogAction(action: UiState.Dialog.Action) {
        when (action) {
            UiState.Dialog.Action.Dismiss -> dismissDialog()
            UiState.Dialog.Action.No -> dismissDialog()
            UiState.Dialog.Action.DeleteYes -> onClickDeleteExecution()
            UiState.Dialog.Action.DetachPayerYes -> onClickDetachPayerYes()
        }
    }

    private fun onClickDetachPayerYes() {
        dismissDialog()

        viewModelScope.launch {
            val result = paymentCommandUseCase.detachPayer(paymentId)

            when (result) {
                DetachPayerResult.Succeeded -> {
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_successfully))
                }

                DetachPayerResult.Failed ->
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    fun onClickPayerName(id: Int) {
        openPaymentEdit(id)
    }
}