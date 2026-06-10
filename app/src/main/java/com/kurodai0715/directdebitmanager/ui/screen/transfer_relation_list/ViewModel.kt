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

sealed interface UiState {
    object Loading : UiState
    data class Error(val errorMessageRes: Int) : UiState
    data class Success(
        val payments: List<FlattenedTreeItem> = emptyList(),
    ) : UiState
}

@HiltViewModel
class ViewModel @Inject constructor(
    paymentQueryUseCase: PaymentQueryUseCase,
) : ViewModel() {

    private val paymentsAsync = paymentQueryUseCase.loadPayments()
        .map { payments ->
            Async.Success(
                payments
                    .buildNestedTree()
                    .flattenTree()
                    // root のノードは表示しない
                    .filterNot { it.id == 0 }
            )
        }
        .catch<Async<List<FlattenedTreeItem>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<UiState> = paymentsAsync.map { paymentsAsync ->
        when (paymentsAsync) {
            is Async.Loading -> {
                UiState.Loading
            }

            is Async.Error -> {
                UiState.Error(paymentsAsync.errorMessage)
            }

            is Async.Success -> {
                Log.d(TAG, "paymentsAsync.data: ${paymentsAsync.data}")
                UiState.Success(paymentsAsync.data)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = UiState.Loading
    )

}
