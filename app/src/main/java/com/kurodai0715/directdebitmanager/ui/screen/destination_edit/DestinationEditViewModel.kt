package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.domain.BasicTextValidator
import com.kurodai0715.directdebitmanager.domain.DestInputType
import com.kurodai0715.directdebitmanager.domain.TransferItemType
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

// TODO UI 状態はドメインモデルに依存するべきではないため、修正が必要。
//  UI が依存しても良いのは、 UI Model のみ。それ以外の場合はプリミティブ型のみでデータを扱う。
data class DestinationEditUiState(
    val destIdFromKeyboard: Int = 0,
    val destIdFromDialog: Int? = null,
    val destNameFromKeyboard: String = "",
    val destNameFromDialog: String = "",
    val destItemTypeFromDialog: TransferItemType? = null,
    val sourceId: Int = 0,
    val sourceName: String = "",
    val sources: List<Source> = emptyList(),
    val destInputType: DestInputType = DestInputType.Keyboard, // TODO 未使用なので削除するべき？使うべき？
    val destInputTypeIndex: Int = 0,
    val destInputTypes: List<DestInputType> = DestInputType.getList(),
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val userMessage: Int? = null,
    val showDelNotAllowedDialog: Boolean = false,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val sourceListDialogType: SourceListDialogType? = null,
    val shouldNavigateToSourceList: Boolean = false,
    val shouldNavigateToSourceEdit: Boolean = false,
    val navigationUpEventConsumed: Boolean = true,
    val isLoading: Boolean = false,
    val destErrorMessage: Int? = null,
    val sourceErrorMessage: Int? = null,
)


data class DestinationUiModel(
    /**
     * ID.
     *
     * ID を自動採番したい場合は 0 を設定してください。
     */
    val id: Int = 0,

    val name: String,

    val sourceId: Int,

//    val date: String,
//    val amount: String,
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

    fun initialize(destId: Int?) {
        viewModelScope.launch {
            if (destId != null) {
                val item = directDebitDefRepo.loadTransferItem(destId)

                _uiState.update {
                    if (item.destination.isSourceItem) {
                        it.copy(
                            destIdFromDialog = item.destination.id,
                            destNameFromDialog = item.destination.label,
                            destInputTypeIndex = 1,
                            sourceId = item.destination.parentId ?: 0,
                            sourceName = item.sourceName,
                            destItemTypeFromDialog = TransferItemType.fromInt(item.destination.type!!)
                        )
                    } else {
                        it.copy(
                            destIdFromKeyboard = item.destination.id,
                            destNameFromKeyboard = item.destination.label,
                            destInputTypeIndex = 0,
                            sourceId = item.destination.parentId ?: 0,
                            sourceName = item.sourceName,
                        )
                    }
                }
            }
        }
    }

    private fun updateSourceString(sourceId: Int, sources: List<Source>): String {
        for (source in sources) {
            if (source.id == sourceId) {
                return source.name
            }
        }
        return ""
    }

    fun updateKeyboardInputDest(dest: String) {
        _uiState.update {
            it.copy(destNameFromKeyboard = dest)
        }
    }

    fun updateDialogSelectionDest(destId: Int, destName: String, destItemType: TransferItemType) {
        _uiState.update {
            it.copy(
                destIdFromDialog = destId,
                destNameFromDialog = destName,
                destItemTypeFromDialog = destItemType,
            )
        }
    }

    fun updateSource(sourceId: Int) {
        _uiState.update {
            it.copy(
                sourceId = sourceId,
            )
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

    fun updateSourceListDialogType(type: SourceListDialogType?) {
        _uiState.update {
            it.copy(sourceListDialogType = type)
        }
    }

    fun updateShouldNavigateToSourceList(value: Boolean) {
        _uiState.update {
            it.copy(shouldNavigateToSourceList = value)
        }
    }

    fun updateShouldNavigateToSourceEdit(value: Boolean) {
        _uiState.update {
            it.copy(shouldNavigateToSourceEdit = value)
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
        val validationResult = BasicTextValidator.validate(getDestName())

        val message = when (validationResult) {
            ValidationResult.EmptyError -> R.string.common_required_field
            ValidationResult.LengthWithin100Error -> R.string.common_length_needs_to_be_within_100
            else -> null
        }

        updateDestErrorMessage(message)

        return validationResult == ValidationResult.Valid
    }

    private fun getDestName(): String {
        val destInputTypeIndex = uiState.value.destInputTypeIndex

        return when (destInputTypeIndex) {
            0 -> uiState.value.destNameFromKeyboard
            1 -> uiState.value.destNameFromDialog
            else -> throw IllegalStateException("Unexpected value: $destInputTypeIndex")
        }
    }

    val destId: Int
        get() {
            val destInputTypeIndex = uiState.value.destInputTypeIndex

            return when (destInputTypeIndex) {
                0 -> uiState.value.destIdFromKeyboard
                1 -> uiState.value.destIdFromDialog
                    ?: throw IllegalStateException("dialogSelectionDestId is null")

                else -> throw IllegalStateException("Unexpected value: $destInputTypeIndex")
            }
        }

    private fun sourceValidation(): Boolean {
        val validationResult = BasicTextValidator.validate(uiState.value.sourceName)
        val message = when (validationResult) {
            ValidationResult.EmptyError -> R.string.common_required_field
            ValidationResult.LengthWithin100Error -> R.string.common_length_needs_to_be_within_100
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
                id = destId,
                label = getDestName(),
                isSourceItem = uiState.value.destInputTypeIndex == 1,
                parentId = uiState.value.sourceId,
                type = uiState.value.destItemTypeFromDialog,
            )

            _uiState.update {
                if (resultSuccess) {
                    // 新規作成 or 更新が成功した場合
                    if (uiState.value.destIdFromKeyboard == 0) {
                        // 新規作成の場合
                        it.copy(
                            destNameFromKeyboard = "",
                            destNameFromDialog = "",
                            sourceId = 0,
                            userMessage = R.string.common_register_successfully
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

    fun checkRelatedDataExistence() {
        viewModelScope.launch {
            // destinationId を振替元として使用している振替先データの件数
            val relatedDestCount = directDebitDefRepo.countDestinationsReferencing(destId)

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
            val numOfDeleted = directDebitDefRepo.deleteDestination(
                id = destId,
                dest = getDestName(),
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

    fun updateDestInputTypeIndex(index: Int) {
        _uiState.update {
            it.copy(destInputTypeIndex = index)
        }
    }

}
