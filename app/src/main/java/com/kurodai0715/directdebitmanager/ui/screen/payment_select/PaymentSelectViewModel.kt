package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface UiStateV2 {
    object Loading : UiStateV2
    data class Error(val errorMessageRes: Int) : UiStateV2
    data class Success(
        val payments: List<Item> = emptyList(),
    ) : UiStateV2 {
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

    val uiStateV2: StateFlow<UiStateV2> = TODO()

    fun onClickItem(payment: UiStateV2.Success.Item) {
        TODO()
    }
}