package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
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
        val selectionState: SelectionState = SelectionState.None,
        val payments: List<Item> = emptyList(),
    ) : PaymentSelectUiState() {
        data class Item(
            val id: Int,
            val name: String,
        )

        fun isSelected(itemId: Int): Boolean {
            return when (val state = selectionState) {
                SelectionState.None -> false
                is SelectionState.Selected -> state.id == itemId
            }
        }
    }

    val canSelect: Boolean
        get() {
            return (this is Success)
                    &&
                    when (selectionState) {
                        SelectionState.None -> false
                        is SelectionState.Selected -> true
                    }
        }
}

sealed interface SelectionState {
    data object None : SelectionState
    data class Selected(val id: Int) : SelectionState
}

@HiltViewModel
class PaymentSelectViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    paymentQueryUseCase: PaymentQueryUseCase,
    private val paymentCommandUseCase: PaymentCommandUseCase,
) : ViewModel() {

    private val paymentId: Int = savedStateHandle
        .toRoute<PaymentSelect>()
        .paymentId

    private val selectedId: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val asyncPayments = paymentQueryUseCase.loadPayments()
        .map { Async.Success(it.toPaymentSelect()) }
        .catch<Async<List<PaymentSelectUiState.Success.Item>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<PaymentSelectUiState> =
        combine(asyncPayments, selectedId) { asyncPayments, selectedId ->
            when (asyncPayments) {
                is Async.Loading -> {
                    PaymentSelectUiState.Loading
                }

                is Async.Error -> {
                    PaymentSelectUiState.Error(asyncPayments.errorMessage)
                }

                is Async.Success -> {
                    val selectionState =
                        selectedId?.let { SelectionState.Selected(it) }
                            ?: SelectionState.None

                    PaymentSelectUiState.Success(
                        selectionState = selectionState,
                        payments = asyncPayments.data
                    )
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

    /**
     * 選択ボタンタップ時に、選択されたアイテムの ID を返す.
     */
    fun onClickSelect(): Int? {
        return when (uiState.value) {
            is PaymentSelectUiState.Success -> {
                selectedId.value
            }

            else -> {
                null
            }
        }
    }

    fun onClickSave() {
        viewModelScope.launch {
            paymentCommandUseCase.addPayer(paymentId = paymentId, payerId = selectedId.value!!)
        }
    }
}