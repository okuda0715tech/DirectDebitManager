package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.model.AddPayerResult
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.navigation.NavContract
import com.kurodai0715.directdebitmanager.ui.navigation.PaymentSelect
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentSelectUiState {
    object Loading : PaymentSelectUiState()
    data class Error(val errorMessageRes: Int) : PaymentSelectUiState()
    data class Success(
        val paymentName: String = "",
        val explainMessageRes: Int,
        val selectedId: Int? = null,
        val payments: List<Item> = emptyList(),
        val dialog: Dialog = Dialog.None,
    ) : PaymentSelectUiState() {
        data class Item(
            val id: Int,
            val name: String,
            val state: ItemState,
        )

        sealed interface Dialog {
            data object None : Dialog
            data object SaveSuccess : Dialog
            data object SaveFailed : Dialog
        }
    }

    val saveButtonEnabled: Boolean
        get() {
            return (this is Success)
                    && selectedId != null
        }
}

sealed class UiEvent {
    data object BackToRelationEditScreen : UiEvent()
}

@HiltViewModel
class ViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    paymentQueryUseCase: PaymentQueryUseCase,
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    private val paymentId: Int = savedStateHandle.toRoute<PaymentSelect>().paymentId

    private val asyncPayment = paymentQueryUseCase.loadPaymentByV2(paymentId)
        .map {
            requireNotNull(it) { "The paymentId cannot be null." }
            Async.Success(it)
        }
        .catch<Async<PaymentEntityV2>> {
            emit(Async.Error(R.string.load_error))
        }

    private val target = NavContract.SelectTarget.valueOf(
        savedStateHandle.toRoute<PaymentSelect>().target
    )

    val explainMessageRes =
        when (target) {
            NavContract.SelectTarget.Payer ->
                R.string.payer_select_explain_label

            NavContract.SelectTarget.Payee ->
                R.string.payee_select_explain_label
        }

    private val selectedId: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val asyncPayments = paymentQueryUseCase.loadPayments()
        .map { payments ->
            val data = payments
                // 自分自身はリストから除外する(支払先や支払元に自分自身が設定されるのはおかしいため)
                .filter { it.id != paymentId }
                .toPaymentSelectUiModel(paymentId)
            Async.Success(data)
        }
        .catch<Async<List<PaymentSelectUiState.Success.Item>>> {
            emit(Async.Error(R.string.load_error))
        }

    private val dialog =
        MutableStateFlow<PaymentSelectUiState.Success.Dialog>(
            PaymentSelectUiState.Success.Dialog.None
        )

    val uiState: StateFlow<PaymentSelectUiState> =
        combine(
            asyncPayments,
            selectedId,
            asyncPayment,
            dialog,
        ) { asyncPayments, selectedId, asyncPayment, dialog ->
            val error = listOf(asyncPayments, asyncPayment)
                .filterIsInstance<Async.Error>()
                .firstOrNull()

            when {
                error != null -> {
                    PaymentSelectUiState.Error(error.errorMessage)
                }

                // 【技術メモ】xxx.data を取得するために、個別の 'is Async.Success' 判定が必要です。
                asyncPayments is Async.Success
                        && asyncPayment is Async.Success -> {

                    val payments = asyncPayments.data.map { listItem ->
                        when {
                            // 編集中の支払情報に対する支払元として登録されている場合
                            listItem.id == asyncPayment.data.parentId -> {
                                listItem.copy(state = ItemState.Registered)
                            }

                            listItem.id == selectedId -> {
                                listItem.copy(state = ItemState.Selected)
                            }

                            else -> listItem
                        }
                    }

                    PaymentSelectUiState.Success(
                        paymentName = asyncPayment.data.label,
                        explainMessageRes = explainMessageRes,
                        selectedId = selectedId,
                        payments = payments,
                        dialog = dialog,
                    )
                }

                else -> {
                    PaymentSelectUiState.Loading
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = PaymentSelectUiState.Loading
        )

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onClickItem(payment: PaymentSelectUiState.Success.Item) {
        selectedId.update { current ->
            if (current == payment.id) null else payment.id
        }
    }

    fun onClickSave(selectedId: Int) {
        viewModelScope.launch {
            val result = when (target) {
                NavContract.SelectTarget.Payer ->
                    paymentCommandUseCase.addPayer(
                        paymentId = paymentId,
                        payerId = selectedId,
                    )

                NavContract.SelectTarget.Payee ->
                    paymentCommandUseCase.addPayer(
                        paymentId = selectedId,
                        payerId = paymentId,
                    )
            }

            when (result) {
                AddPayerResult.Succeeded -> {
                    updateDialog(PaymentSelectUiState.Success.Dialog.SaveSuccess)
                }

                AddPayerResult.Failed -> {
                    updateDialog(PaymentSelectUiState.Success.Dialog.SaveFailed)
                }
            }
        }
    }

    private fun updateDialog(nextState: PaymentSelectUiState.Success.Dialog) {
        dialog.update { nextState }
    }

    fun onClickSaveDialogClose() {
        updateDialog(PaymentSelectUiState.Success.Dialog.None)

        viewModelScope.launch {
            _eventChannel.send(UiEvent.BackToRelationEditScreen)
        }
    }
}