package com.kurodai0715.directdebitmanager.ui.source_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.Source
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SourceEditUiState(
    val sourceId: Int = 0,
    val sourceName: String = "",
    val userMessage: Int? = null,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val navigationUpEventConsumed: Boolean = true,
)

@HiltViewModel
class SourceEditViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(SourceEditUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<SourceEditUiState> = _uiState.asStateFlow()

    fun updateSource(source: String) {
        _uiState.update {
            it.copy(sourceName = source)
        }
    }

    fun updateTransSource(source: Source) {
        _uiState.update {
            it.copy(
                sourceId = source.id,
                sourceName = source.name,
            )
        }
    }

    fun updateDelConfDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showDelConfDialog = show)
        }
    }

    fun updateDelCompDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showDelCompDialog = show)
        }
    }

    fun updateNavigateUpEventConsumed(value: Boolean) {
        _uiState.update {
            it.copy(navigationUpEventConsumed = value)
        }
    }

    fun saveData() {
        viewModelScope.launch {
            val resultSuccess = directDebitDefRepo.upsertSource(
                id = uiState.value.sourceId,
                source = uiState.value.sourceName
            )

            _uiState.update {
                if (resultSuccess) {
                    // 新規作成 or 更新が成功した場合
                    if (uiState.value.sourceId == 0) {
                        // 新規作成の場合
                        it.copy(
                            sourceName = "",
                            userMessage = R.string.common_save_successfully
                        )
                    } else {
                        // 更新の場合
                        it.copy(
                            userMessage = R.string.common_update_successfully
                        )
                    }
                } else {
                    // 新規作成 or 更新が失敗した場合
                    it.copy(
                        userMessage = R.string.common_save_failed
                    )
                }
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            val numOfDeleted = directDebitDefRepo.deleteSource(
                id = uiState.value.sourceId,
                name = uiState.value.sourceName
            )

            _uiState.update {
                if (numOfDeleted > 0) {
                    // 削除に成功した場合

                    it.copy(
                        showDelCompDialog = true,
                    )
                } else {
                    // 削除に失敗した場合

                    it.copy(
                        userMessage = R.string.common_delete_failed,
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update {
            it.copy(
                userMessage = null
            )
        }
    }

}