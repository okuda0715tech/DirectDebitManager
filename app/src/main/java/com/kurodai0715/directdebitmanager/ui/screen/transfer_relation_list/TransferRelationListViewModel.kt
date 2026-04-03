package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import android.util.Log
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

private const val TAG = "TransferRelationListViewModel.kt"

sealed interface TransferRelationListUiState {
    object Loading : TransferRelationListUiState
    data class Error(val errorMessageRes: Int) : TransferRelationListUiState
    data class Success(
        val payments: List<FlattenedTreeItem> = emptyList(),
    ) : TransferRelationListUiState {
        data class Item(
            val id: Int,
            val name: String,
            val payerId: Int? = null,
        )
    }
}

@HiltViewModel
class TransferRelationListViewModel @Inject constructor(
    paymentQueryUseCase: PaymentQueryUseCase,
) : ViewModel() {

    private val paymentsAsync = paymentQueryUseCase.loadPayments()
        .map { Async.Success(it.buildNestedTree().flattenTree()) }
        .catch<Async<List<FlattenedTreeItem>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<TransferRelationListUiState> = paymentsAsync.map { paymentsAsync ->
        when (paymentsAsync) {
            is Async.Loading -> {
                TransferRelationListUiState.Loading
            }

            is Async.Error -> {
                TransferRelationListUiState.Error(paymentsAsync.errorMessage)
            }

            is Async.Success -> {
                Log.d(TAG, "paymentsAsync.data: ${paymentsAsync.data}")
                TransferRelationListUiState.Success(paymentsAsync.data)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TransferRelationListUiState.Loading
    )

}
