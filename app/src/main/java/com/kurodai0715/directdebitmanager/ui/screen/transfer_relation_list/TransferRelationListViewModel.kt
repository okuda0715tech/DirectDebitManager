package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import androidx.lifecycle.ViewModel
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.usecase.PaymentQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TransferRelationListUiState(
    val payments: List<Item> = emptyList(),
) {
    data class Item(
        val name: String,
    )
}


@HiltViewModel
class TransferRelationListViewModel @Inject constructor(
    paymentQueryUseCase: PaymentQueryUseCase,
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(TransferRelationListUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<TransferRelationListUiState> = _uiState.asStateFlow()
    private val paymentsAsync = paymentQueryUseCase.loadPayments()
        .map { Async.Success(it.toTransferRelationList()) }
        .catch<Async<List<TransferRelationListUiState.Item>>> {
            emit(Async.Error(R.string.load_error))
        }


}
