package com.kurodai0715.directdebitmanager.ui.direct_debit_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "DirectDebitListViewModel.kt"

data class DirectDebitsUiState(
    val items: List<Destination> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class DirectDebitListViewModel @Inject constructor(
    directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val _destinationAsync = directDebitDefRepo.fetchDirectDebitStream()
        .map { Async.Success(it) }
        .catch<Async<List<Destination>>> { e ->
            Log.e(TAG, "fetchDirectDebitStream failed.", e)
            emit(Async.Error(R.string.fetch_error)) }

    val uiState: StateFlow<DirectDebitsUiState> =
        combine(_destinationAsync, _userMessage) { directDebitAsync, userMessage ->

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
                        userMessage = userMessage,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DirectDebitsUiState(isLoading = true)
        )

    fun snackbarMessageShown() {
        _userMessage.value = null
    }
}