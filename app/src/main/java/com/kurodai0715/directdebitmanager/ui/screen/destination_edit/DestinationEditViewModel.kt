package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.domain.BasicTextValidator
import com.kurodai0715.directdebitmanager.domain.ValidationResult
import com.kurodai0715.directdebitmanager.ui.util.Async
import com.kurodai0715.directdebitmanager.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "DestinationEditViewModel.kt"

data class DestinationEditUiState(
    val destId: Int = 0,
    val destName: String = "",
    val sourceId: Int = 0,
    val sourceName: String = "",
    val sources: List<Source> = emptyList(),
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val userMessage: Int? = null,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val showSourceListDialog: Boolean = false,
    val addEditSourceListEventConsumed: Boolean = true,
    val navigationUpEventConsumed: Boolean = true,
    val isLoading: Boolean = false,
    val destErrorMessage: Int? = null,
    val sourceErrorMessage: Int? = null,
)


@HiltViewModel
class DestinationEditViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(DestinationEditUiState())

    private val _sourcesAsync = directDebitDefRepo.fetchSourcesStream()
        .map { Async.Success(it) }
        .catch<Async<List<Source>>> {
            Log.e(TAG, "Failed to read trans sources.", it)
            emit(Async.Error(R.string.fetch_error))
        }

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<DestinationEditUiState> =
        combine(_sourcesAsync, _uiState) { transSourcesAsync, uiState ->
            when (transSourcesAsync) {
                is Async.Loading -> {
                    uiState.copy(isLoading = true)
                }

                is Async.Error -> {
                    uiState.copy(userMessage = transSourcesAsync.errorMessage)
                }

                is Async.Success -> {
                    uiState.copy(
                        sourceName = updateSourceString(
                            sourceId = uiState.sourceId,
                            sources = transSourcesAsync.data
                        ),
                        sources = transSourcesAsync.data,
                        isLoading = false,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DestinationEditUiState(isLoading = true)
        )

    private fun updateSourceString(sourceId: Int, sources: List<Source>): String {
        for (source in sources) {
            if (source.id == sourceId) {
                return source.name
            }
        }
        return ""
    }

    fun updateDest(dest: String) {
        _uiState.update {
            it.copy(destName = dest)
        }
    }

    fun updateSource(sourceId: Int) {
        _uiState.update {
            it.copy(
                sourceId = sourceId,
            )
        }
    }

    fun updateDirectDebit(destination: Destination) {
        _uiState.update {
            it.copy(
                destId = destination.id,
                destName = destination.name,
                sourceId = destination.sourceId,
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

    fun updateSourceListDialogVisibility(show: Boolean) {
        _uiState.update {
            it.copy(showSourceListDialog = show)
        }
    }

    fun updateAddEditSourceListEventConsumed(value: Boolean) {
        _uiState.update {
            it.copy(addEditSourceListEventConsumed = value)
        }
    }

    fun updateNavigateUpEventConsumed(value: Boolean) {
        _uiState.update {
            it.copy(navigationUpEventConsumed = value)
        }
    }

    fun updateDestErrorMessage(message: Int?) {
        _uiState.update {
            it.copy(
                destErrorMessage = message
            )
        }
    }

    fun updateSourceErrorMessage(message: Int?) {
        _uiState.update {
            it.copy(
                sourceErrorMessage = message
            )
        }
    }

    fun validate() {
        val destValidationSuccess = destValidation()
        val sourceValidationSuccess = sourceValidation()

        if (!destValidationSuccess) return
        if (!sourceValidationSuccess) return

        saveData()
    }

    private fun destValidation(): Boolean {
        val validationResult = BasicTextValidator.validate(uiState.value.destName)
        val message = when (validationResult) {
            ValidationResult.EmptyError -> R.string.common_required_field
            ValidationResult.LengthWithin30Error -> R.string.common_length_needs_to_be_within_30
            else -> null
        }

        updateDestErrorMessage(message)

        return validationResult == ValidationResult.Valid
    }

    private fun sourceValidation(): Boolean {
        val validationResult = BasicTextValidator.validate(uiState.value.sourceName)
        val message = when (validationResult) {
            ValidationResult.EmptyError -> R.string.common_required_field
            ValidationResult.LengthWithin30Error -> R.string.common_length_needs_to_be_within_30
            else -> null
        }
        updateSourceErrorMessage(message)

        return validationResult == ValidationResult.Valid
    }

//    fun updateDate(date: String) {
//        _uiState.update {
//            it.copy(transferDate = date)
//        }
//    }
//
//    fun updateAmount(amount: String) {
//        _uiState.update {
//            it.copy(transferAmount = amount)
//        }
//    }

    private fun saveData() {
        viewModelScope.launch {
            val resultSuccess = directDebitDefRepo.upsertDestination(
                id = uiState.value.destId,
                dest = uiState.value.destName,
                sourceId = uiState.value.sourceId,
            )

            _uiState.update {
                if (resultSuccess) {
                    // 新規作成 or 更新が成功した場合
                    if (uiState.value.destId == 0) {
                        // 新規作成の場合
                        it.copy(
                            destName = "",
                            sourceId = 0,
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
            val numOfDeleted = directDebitDefRepo.deleteDestination(
                id = uiState.value.destId,
                dest = uiState.value.destName,
                sourceId = uiState.value.sourceId,
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