package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.navigation.NavContract
import com.kurodai0715.directdebitmanager.ui.navigation.PaymentSelect
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentSelectUiState {
    object Loading : PaymentSelectUiState()
    data class Error(val errorMessageRes: Int) : PaymentSelectUiState()
    data class Success(
        val selectedId: Int? = null,
        val payments: List<Item> = emptyList(),
    ) : PaymentSelectUiState() {
        data class Item(
            val id: Int,
            val name: String,
            val state: ItemState,
        )
    }

    val saveButtonEnabled: Boolean
        get() {
            return (this is Success)
                    && selectedId != null
        }
}

@HiltViewModel
class ViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    paymentQueryUseCase: PaymentQueryUseCase,
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    private val paymentId: Int = savedStateHandle.toRoute<PaymentSelect>().paymentId

    private val payerId: Int = savedStateHandle.toRoute<PaymentSelect>().payerId

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

    private val selectedId: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val asyncPayments = paymentQueryUseCase.loadPayments()
        .map { payments ->
            val data = payments
                // 自分自身はリストから除外する(支払先や支払元に自分自身が設定されるのはおかしいため)
                .filter { it.id != paymentId }
                .toPaymentSelectUiModel(paymentId, payerId)
            Async.Success(data)
        }
        .catch<Async<List<PaymentSelectUiState.Success.Item>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<PaymentSelectUiState> =
        combine(
            asyncPayments,
            selectedId,
            asyncPayment
        ) { asyncPayments, selectedId, asyncPayment ->
            val error = listOf(asyncPayments, asyncPayment)
                .filterIsInstance<Async.Error>()
                .firstOrNull()

            when {
                error != null -> {
                    PaymentSelectUiState.Error(error.errorMessage)
                }

                asyncPayments is Async.Success
                        && asyncPayment is Async.Success -> {

                    val payments = asyncPayments.data.map {
                        if (it.id == selectedId) {
                            it.copy(state = ItemState.Selected)
                        } else {
                            it
                        }
                    }

                    PaymentSelectUiState.Success(
                        selectedId = selectedId,
                        payments = payments,
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

    fun onClickItem(payment: PaymentSelectUiState.Success.Item) {
        selectedId.update { current ->
            if (current == payment.id) null else payment.id
        }
    }

    fun onClickSave(selectedId: Int) {
        viewModelScope.launch {
            when (target) {
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
        }
    }
}