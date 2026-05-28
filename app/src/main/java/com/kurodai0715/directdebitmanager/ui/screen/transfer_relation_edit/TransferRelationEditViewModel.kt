package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.DeleteResult
import com.kurodai0715.directdebitmanager.domain.model.PayeeId
import com.kurodai0715.directdebitmanager.domain.model.PayerId
import com.kurodai0715.directdebitmanager.domain.model.Payment
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PaymentEditViewModel.kt"

// TODO ストリームを構築し、常に最新の支払情報を画面に表示する。
data class UiState(
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

sealed class UiEvent {
    data class ShowSnackbar(val messageRes: Int) : UiEvent()
    data class OnClickPayment(val paymentId: Int) : UiEvent()
    data object OnClickPayer : UiEvent()
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

    /**
     * ユーザーが支払情報編集画面に直接入力した値.
     */
    private val payment = MutableStateFlow(UiState.Payment())

    private val payer: MutableStateFlow<UiState.Payer> =
        MutableStateFlow(UiState.Payer.Unassigned)

    private val payees = MutableStateFlow<List<UiState.Payee>>(emptyList())

    private val dialog = MutableStateFlow<UiState.Dialog?>(null)

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

            payment.update {
                it.copy(
                    id = UiState.Payment.Id.Assigned(loadedPayment.id.value),
                    name = loadedPayment.name.value
                )
            }

            if (loadedPayment.payerId.isValid)
                addPayer(loadedPayment.payerId.value)

            loadPayees(loadedPayment.id.value)
        }
    }

    fun save() {
        viewModelScope.launch {
            val result = saveRelations(
                paymentId = uiState.value.getPaymentId(),
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

    fun onClickDetachPayer() {
        removePayer()
    }

    private fun removePayer() {
        payer.update { UiState.Payer.Unassigned }
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
        dialog.update { null }
    }

    fun onClickDeleteExecution() {
        viewModelScope.launch {
            val result = deletePayment(uiState.value.paymentToDomain2())

            when (result) {
                DeleteResult.Succeeded -> {
                    _eventChannel.send(UiEvent.OnDeleted)
                }

                DeleteResult.Failed ->
                    TODO()
            }
        }
    }

    private suspend fun deletePayment(payment: Payment.Persisted): DeleteResult {
        return paymentCommandUseCase.deletePayment(payment)
    }

    fun onClickPayment() {
        when (val id = payment.value.id) {

            is UiState.Payment.Id.Assigned -> {
                viewModelScope.launch {
                    _eventChannel.send(UiEvent.OnClickPayment(id.value))
                }
            }

            else -> {
                throw IllegalStateException("paymentId is Unassigned.")
            }
        }
    }
}