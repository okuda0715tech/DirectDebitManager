package com.kurodai0715.directdebitmanager.ui.direct_debit_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DirectDebitsUiState(
    val items: List<DirectDebit> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class DirectDebitListViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    private val _directDebitAsync = directDebitDefRepo.fetchDirectDebitStream()
        .map { Async.Success(it) }
        .catch<Async<List<DirectDebit>>> { emit(Async.Error(R.string.fetch_error)) }

    val uiState: StateFlow<DirectDebitsUiState> = _directDebitAsync.map { directDebitAsync ->
        when (directDebitAsync) {
            is Async.Loading -> {
                DirectDebitsUiState(isLoading = true)
            }

            is Async.Error -> {
                DirectDebitsUiState(userMessage = directDebitAsync.errorMessage)
            }

            is Async.Success -> {
                DirectDebitsUiState(
                    items = directDebitAsync.data,
                    isLoading = false,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = DirectDebitsUiState(isLoading = true)
    )

}