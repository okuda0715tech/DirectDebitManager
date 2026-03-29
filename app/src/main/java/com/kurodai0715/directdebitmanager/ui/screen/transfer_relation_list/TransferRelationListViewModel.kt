package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TransferRelationListUiState(
    val payments: List<Item> = emptyList(),
    val isLoading: Boolean = false,
) {
    data class Item(
        val name: String,
    )
}


@HiltViewModel
class TransferRelationListViewModel @Inject constructor(
    paymentQueryUseCase: PaymentQueryUseCase,
) : ViewModel() {

    private val paymentsAsync = paymentQueryUseCase.loadPayments()
        .map { Async.Success(it.toTransferRelationList()) }
        .catch<Async<List<TransferRelationListUiState.Item>>> {
            emit(Async.Error(R.string.load_error))
        }

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(TransferRelationListUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<TransferRelationListUiState> = paymentsAsync.map { paymentsAsync ->
        when (paymentsAsync) {
            is Async.Loading -> {
                TransferRelationListUiState(isLoading = true)
            }

            is Async.Error -> {
                // TODO エラーメッセージをキューに追加する
                TransferRelationListUiState()
            }

            is Async.Success -> {
                TransferRelationListUiState(
                    payments = paymentsAsync.data
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TransferRelationListUiState(isLoading = true)
    )


}
