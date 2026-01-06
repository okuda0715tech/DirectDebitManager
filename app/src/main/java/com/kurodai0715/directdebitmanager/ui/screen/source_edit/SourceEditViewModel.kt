/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.domain.BasicTextValidator
import com.kurodai0715.directdebitmanager.domain.ValidationResult
import com.kurodai0715.directdebitmanager.domain.model.ItemType
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
    val parentId: Int = 0,
    val userMessage: Int? = null,
    val showDelNotAllowedDialog: Boolean = false,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val showSourceTypeListDialog: Boolean = false,
    val shouldNavigateUp: Boolean = false,
    val sourceErrorMessage: Int? = null,
    val sourceType: ItemType = ItemType.Bank,
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

    fun initialize(sourceId: Int?) {
        viewModelScope.launch {
            if (sourceId != null) {
                val item = directDebitDefRepo.loadItem(sourceId)

                _uiState.update {
                    it.copy(
                        sourceId = item.id,
                        sourceName = item.label,
                        sourceType = ItemType.fromInt(item.type!!),
                        parentId = item.parentId,
                    )
                }
            }
        }
    }

    fun updateDelNotAllowedDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showDelNotAllowedDialog = show)
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

    fun updateSourceTypeListDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showSourceTypeListDialog = show)
        }
    }

    fun updateShouldNavigateUp(value: Boolean) {
        _uiState.update {
            it.copy(shouldNavigateUp = value)
        }
    }

    fun updateSourceType(type: ItemType) {
        _uiState.update {
            it.copy(sourceType = type)
        }
    }

    fun validate() {
        val sourceValidationSuccess = sourceValidation()

        if (!sourceValidationSuccess) return

        saveData()
    }

    private fun sourceValidation(): Boolean {
        val validationResult = BasicTextValidator.validate(uiState.value.sourceName)
        val message = when (validationResult) {
            ValidationResult.EmptyError -> R.string.common_required_field
            ValidationResult.LengthWithin100Error -> R.string.common_length_needs_to_be_within_100
            else -> null
        }

        _uiState.update {
            it.copy(
                sourceErrorMessage = message
            )
        }

        return validationResult == ValidationResult.Valid
    }

    private fun saveData() {
        viewModelScope.launch {
            val resultSuccess = directDebitDefRepo.upsertSource(
                id = uiState.value.sourceId,
                name = uiState.value.sourceName,
                type = uiState.value.sourceType.value,
                parentId = uiState.value.parentId
            )

            val isNewlyCreated = uiState.value.sourceId == 0

            _uiState.update {
                when {
                    // 新規作成が成功した場合
                    resultSuccess && isNewlyCreated -> {
                        it.copy(
                            sourceName = "",
                            userMessage = R.string.common_register_successfully
                        )
                    }

                    // 更新が成功した場合
                    resultSuccess -> {
                        it.copy(
                            userMessage = R.string.common_update_successfully
                        )
                    }

                    // 新規作成 or 更新が失敗した場合
                    else -> {
                        it.copy(
                            userMessage = R.string.common_save_failed
                        )
                    }
                }
            }
        }
    }

    fun checkRelatedDataExistence(sourceId: Int) {
        viewModelScope.launch {
            // sourceId を振替元として使用している振替先データの件数
            val relatedDestCount = directDebitDefRepo.countDestinationsReferencing(sourceId)

            _uiState.update {
                when (relatedDestCount) {
                    0 ->
                        it.copy(showDelConfDialog = true)

                    in 1..Int.MAX_VALUE ->
                        it.copy(showDelNotAllowedDialog = true)

                    -1 ->
                        it.copy(userMessage = R.string.common_unexpected_error)

                    else ->
                        it // 予期しない値が来た場合は変更なし
                }
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            val deletedSourceCount = directDebitDefRepo.deleteItemBy(id = uiState.value.sourceId)

            val resultFailure = deletedSourceCount == -1

            _uiState.update {
                if (resultFailure) {
                    // 削除に失敗した場合
                    it.copy(userMessage = R.string.common_delete_failed)
                } else {
                    // 削除に成功した場合
                    it.copy(showDelCompDialog = true)
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