package com.kurodai0715.directdebitmanager.ui.screen.payee_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.usecase.PayeeQueryUseCase
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PayeeListUiState(
    val items: List<PayeeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessageRes: Int? = null
)

data class PayeeUiModel(
    val id: Int,
    val name: String,
)

@HiltViewModel
class PayeeListViewModel @Inject constructor(
    payeeQueryUseCase: PayeeQueryUseCase
) : ViewModel() {

    private val _payeesAsync = payeeQueryUseCase.loadPayees()
        .map { Async.Success(it.toPayeeUiModels()) }
        .catch<Async<List<PayeeUiModel>>> {
            emit(Async.Error(R.string.load_error))
        }

    val uiState: StateFlow<PayeeListUiState> = _payeesAsync.map { payeeAsync ->
        when (payeeAsync) {
            is Async.Loading -> {
                PayeeListUiState(isLoading = true)
            }

            is Async.Error -> {
                PayeeListUiState(userMessageRes = payeeAsync.errorMessage)
            }

            is Async.Success -> {
                PayeeListUiState(
                    items = payeeAsync.data,
                    isLoading = false,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = PayeeListUiState(isLoading = true)
    )

}