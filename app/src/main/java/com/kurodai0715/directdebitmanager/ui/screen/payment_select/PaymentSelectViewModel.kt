package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface PaymentSelectUiState {
    object Loading : PaymentSelectUiState
    data class Error(val errorMessageRes: Int) : PaymentSelectUiState
    data class Success(
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

) : ViewModel() {

    val uiStateV2: StateFlow<PaymentSelectUiState> = TODO()

    fun onClickItem(payment: PaymentSelectUiState.Success.Item) {
        TODO()
    }
}