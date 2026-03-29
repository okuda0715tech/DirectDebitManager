package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
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

sealed class TransferRelationListUiEvent {
    data class ShowSnackbar(val messageRes: Int) : TransferRelationListUiEvent()
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

    val uiState: StateFlow<TransferRelationListUiState> = paymentsAsync.map { paymentsAsync ->
        when (paymentsAsync) {
            is Async.Loading -> {
                TransferRelationListUiState(isLoading = true)
            }

            is Async.Error -> {
                _eventChannel.send(
                    TransferRelationListUiEvent.ShowSnackbar(
                        paymentsAsync.errorMessage
                    )
                )
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

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<TransferRelationListUiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()


}
