package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data object TransferRelationListUiState

@HiltViewModel
class TransferRelationListViewModel @Inject constructor() : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(TransferRelationListUiState)

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<TransferRelationListUiState> = _uiState.asStateFlow()

}
