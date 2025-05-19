package com.kurodai0715.directdebitmanager.ui.source_select

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.TransSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "SourceSelectViewModel.kt"

data class SourceSelectUiState(
    val sources: List<TransSource> = emptyList(),
    val userMessage: Int? = null,
    val loading: Boolean = true,
)

@HiltViewModel
class SourceSelectViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(SourceSelectUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<SourceSelectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // 振替元情報を取得し、 UI 画面状態に反映する。
            try {
                val sources = directDebitDefRepo.fetchTransSource()

                _uiState.update {
                    it.copy(
                        sources = sources,
                        loading = false,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _uiState.update {
                    it.copy(
                        userMessage = R.string.fetch_error,
                        loading = false,
                    )
                }
            }
        }
    }
}