package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface PaymentSelectUiState {
    object Loading : PaymentSelectUiState
    data class Error(val errorMessageRes: Int) : PaymentSelectUiState
    data class Success(
        val selectedId: Int? = null,
        val payments: List<Item> = emptyList(),
    ) : PaymentSelectUiState {
        data class Item(
            val id: Int,
            val name: String,
            val selected: Boolean,
        )
    }
}

@HiltViewModel
class PaymentSelectViewModel @Inject constructor(
    paymentQueryUseCase: PaymentQueryUseCase,
) : ViewModel() {

    private val asyncPayments = paymentQueryUseCase.loadPayments()
        .map { Async.Success(it.toPaymentSelect()) }
        .catch<Async<List<PaymentSelectUiState.Success.Item>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<PaymentSelectUiState> = asyncPayments.map { asyncPayments ->
        when (asyncPayments) {
            is Async.Loading -> {
                PaymentSelectUiState.Loading
            }

            is Async.Error -> {
                PaymentSelectUiState.Error(asyncPayments.errorMessage)
            }

            is Async.Success -> {
                PaymentSelectUiState.Success(asyncPayments.data)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = PaymentSelectUiState.Loading
    )

    fun onClickItem(payment: PaymentSelectUiState.Success.Item) {
        TODO()
    }
}