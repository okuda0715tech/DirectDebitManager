/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.DirectDebitDefaultRepository
import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.domain.BasicTextValidator
import com.kurodai0715.directdebitmanager.domain.ValidationResult
import com.kurodai0715.directdebitmanager.domain.model.DestInputType
import com.kurodai0715.directdebitmanager.domain.model.SourceUiModel
import com.kurodai0715.directdebitmanager.domain.model.TransferItemType
import com.kurodai0715.directdebitmanager.ui.dialog.source_selection.SourceListDialogType
import com.kurodai0715.directdebitmanager.ui.dialog.source_selection.SourceSelectionUiModel
import com.kurodai0715.directdebitmanager.ui.dialog.source_selection.toSourceSelectionUiModel
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
    val destIdFromKeyboard: Int = 0,
    val destIdFromDialog: Int? = null,
    val destNameFromKeyboard: String = "",
    val destNameFromDialog: String = "",
    val destItemTypeFromDialog: TransferItemType? = null,
    val sourceId: Int = 0,
    val sourceName: String = "",
    val sources: List<SourceUiModel> = emptyList(),
    val sourceSelectionDialogItems: List<SourceSelectionUiModel> = emptyList(),
    val destInputType: DestInputType = DestInputType.Keyboard, // TODO 未使用なので削除するべき？使うべき？
    val selectedButtonIndex: Int = 0, // セグメントボタンの選択されたボタンのインデックス
    val destInputTypes: List<DestInputType> = DestInputType.getSortedList(),
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val showDelCompDialog: Boolean = false,
    val sourceListDialogType: SourceListDialogType? = null,
    val isLoading: Boolean = false,
    val destErrorMessage: Int? = null,
    val sourceErrorMessage: Int? = null,
    val navigationUiState: NavigationUiState = NavigationUiState(),
    val dialogUiState: DialogUiState = DialogUiState(),
    val messageUiState: MessageUiState = MessageUiState(),
)

data class NavigationUiState(
    val shouldNavigateToSourceList: Boolean = false,
    val shouldNavigateToSourceEdit: Boolean = false,
    val navigationUpEventConsumed: Boolean = true,
)

data class DialogUiState(
    val showDelNotAllowedDialog: Boolean = false,
    val showDelConfDialog: Boolean = false,
)

data class MessageUiState(
    val userMessage: Int? = null,
)

@HiltViewModel
class DestinationEditViewModel @Inject constructor(
    private val directDebitDefRepo: DirectDebitDefaultRepository
) : ViewModel() {

    // TODO DestinationEditUiState のプロパティがすべて他の data class に譲渡できたら、
    //  この _somethingUiState プロパティは削除できる予定。
    /**
     * 更新用.
     */
    private val _somethingUiState = MutableStateFlow(DestinationEditUiState())

    /**
     * 更新用.
     */
    private val _navigationUiState = MutableStateFlow(NavigationUiState())

    /**
     * 更新用.
     */
    private val _dialogUiState = MutableStateFlow(DialogUiState())

    /**
     * 更新用.
     */
    private val _messageUiState = MutableStateFlow(MessageUiState())

    private val _sourcesAsync = directDebitDefRepo.loadSourcesStream()
        .map { Async.Success(it) }
        .catch<Async<List<TransferItemEntity>>> {
            Log.e(TAG, "Failed to read trans sources.", it)
            emit(Async.Error(R.string.load_error))
        }

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<DestinationEditUiState> =
        combine(
            _sourcesAsync,
            _somethingUiState,
            _navigationUiState,
            _dialogUiState,
            _messageUiState,
        ) { transSourcesAsync, uiState, navigationUiState, dialogUiState, messageUiState ->
            when (transSourcesAsync) {
                is Async.Loading -> {
                    DestinationEditUiState(isLoading = true)
                }

                is Async.Error -> {
                    DestinationEditUiState(
                        messageUiState = MessageUiState(
                            userMessage = transSourcesAsync.errorMessage
                        )
                    )
                }

                is Async.Success -> {
                    DestinationEditUiState(
                        destIdFromKeyboard = uiState.destIdFromKeyboard,
                        destIdFromDialog = uiState.destIdFromDialog,
                        destNameFromKeyboard = uiState.destNameFromKeyboard,
                        destNameFromDialog = uiState.destNameFromDialog,
                        destItemTypeFromDialog = uiState.destItemTypeFromDialog,
                        sourceId = uiState.sourceId,
                        destInputType = uiState.destInputType,
                        selectedButtonIndex = uiState.selectedButtonIndex,
                        destInputTypes = uiState.destInputTypes,
                        showDelCompDialog = uiState.showDelCompDialog,
                        sourceListDialogType = uiState.sourceListDialogType,
                        destErrorMessage = uiState.destErrorMessage,
                        sourceErrorMessage = uiState.sourceErrorMessage,
                        sourceName = updateSourceString(
                            sourceId = uiState.sourceId,
                            sources = transSourcesAsync.data.map { it.toSourceUiModel() }
                        ),
                        sources = transSourcesAsync.data.map { it.toSourceUiModel() },
                        sourceSelectionDialogItems = transSourcesAsync.data.toSourceSelectionUiModel(),
                        isLoading = false,
                        navigationUiState = navigationUiState,
                        dialogUiState = dialogUiState,
                        messageUiState = messageUiState,
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

                _somethingUiState.update {
                    if (item.destination.isSourceItem) {
                        it.copy(
                            destIdFromDialog = item.destination.id,
                            destNameFromDialog = item.destination.label,
                            selectedButtonIndex = 1,
                            sourceId = item.destination.parentId,
                            sourceName = item.sourceName,
                            destItemTypeFromDialog = TransferItemType.fromInt(item.destination.type!!)
                        )
                    } else {
                        it.copy(
                            destIdFromKeyboard = item.destination.id,
                            destNameFromKeyboard = item.destination.label,
                            selectedButtonIndex = 0,
                            sourceId = item.destination.parentId,
                            sourceName = item.sourceName,
                        )
                    }
                }
            }
        }
    }

    private fun updateSourceString(sourceId: Int, sources: List<SourceUiModel>): String {
        for (source in sources) {
            if (source.id == sourceId) {
                return source.name
            }
        }
        return ""
    }

    fun updateDest(dest: String) {
        _somethingUiState.update {
            it.copy(destNameFromKeyboard = dest)
        }
    }

    fun updateDest(destId: Int) {
        /*
        【注意】
        uiState と _uiState は別物です。
        DestinationEditUiState.sources は uiState にしか格納されておらず、 _uiState には格納されていません。
         */
        val sources = uiState.value.sources
        val source = checkNotNull(sources.find { it.id == destId }) { "source is null." }

        _somethingUiState.update {
            it.copy(
                destIdFromDialog = destId,
                destNameFromDialog = source.name,
                destItemTypeFromDialog = source.type,
            )
        }
    }

    fun updateSource(sourceId: Int) {
        _somethingUiState.update {
            it.copy(
                sourceId = sourceId,
            )
        }
    }

    fun updateDelNotAllowedDialogVisibility(show: Boolean) {
        _dialogUiState.update {
            it.copy(showDelNotAllowedDialog = show)
        }
    }

    fun updateDelConfDialogVisibility(show: Boolean) {
        _dialogUiState.update {
            it.copy(showDelConfDialog = show)
        }
    }

    fun updateDelCompDialogVisibility(show: Boolean) {
        _somethingUiState.update {
            it.copy(showDelCompDialog = show)
        }
    }

    fun updateSourceListDialogType(type: SourceListDialogType?) {
        _somethingUiState.update {
            it.copy(sourceListDialogType = type)
        }
    }

    fun updateShouldNavigateToSourceList(value: Boolean) {
        _navigationUiState.update {
            it.copy(shouldNavigateToSourceList = value)
        }
    }

    fun updateShouldNavigateToSourceEdit(value: Boolean) {
        _navigationUiState.update {
            it.copy(shouldNavigateToSourceEdit = value)
        }
    }

    fun updateNavigateUpEventConsumed(value: Boolean) {
        _navigationUiState.update {
            it.copy(navigationUpEventConsumed = value)
        }
    }

    fun updateDestErrorMessage(message: Int?) {
        _somethingUiState.update {
            it.copy(
                destErrorMessage = message
            )
        }
    }

    fun updateSourceErrorMessage(message: Int?) {
        _somethingUiState.update {
            it.copy(
                sourceErrorMessage = message
            )
        }
    }

    // TODO 同じ振替先が 2 個以上登録されないようにするチェックを追加する。
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
        val selectedButtonIndex = uiState.value.selectedButtonIndex

        return when (selectedButtonIndex) {
            DestInputType.Keyboard.defaultDisplayOrder -> uiState.value.destNameFromKeyboard
            DestInputType.SourceList.defaultDisplayOrder -> uiState.value.destNameFromDialog
            else -> throw IllegalStateException("Unexpected value: $selectedButtonIndex")
        }
    }

    val destId: Int?
        get() {
            val destInputTypeIndex = uiState.value.selectedButtonIndex

            return when (destInputTypeIndex) {
                DestInputType.Keyboard.defaultDisplayOrder -> uiState.value.destIdFromKeyboard
                DestInputType.SourceList.defaultDisplayOrder -> uiState.value.destIdFromDialog
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
                isSourceItem = uiState.value.selectedButtonIndex == DestInputType.SourceList.defaultDisplayOrder,
                parentId = uiState.value.sourceId,
                type = uiState.value.destItemTypeFromDialog,
            )

            if (resultSuccess) {
                // 新規作成 or 更新が成功した場合
                if (uiState.value.destIdFromKeyboard == 0) {
                    _somethingUiState.update {
                        // 新規作成の場合
                        it.copy(
                            destNameFromKeyboard = "",
                            destNameFromDialog = "",
                            sourceId = 0,
                        )
                    }
                }
            }

            _messageUiState.update {
                if (resultSuccess) {
                    // 新規作成 or 更新が成功した場合
                    if (uiState.value.destIdFromKeyboard == 0) {
                        // 新規作成の場合
                        it.copy(userMessage = R.string.common_register_successfully)
                    } else {
                        // 更新の場合
                        it.copy(userMessage = R.string.common_update_successfully)
                    }
                } else {
                    // 新規作成 or 更新が失敗した場合
                    it.copy(userMessage = R.string.common_save_failed)
                }
            }
        }
    }

    fun checkRelatedDataExistence() {
        viewModelScope.launch {
            // destinationId を振替元として使用している振替先データの件数
            val relatedDestCount = directDebitDefRepo.countDestinationsReferencing(destId)

            when (relatedDestCount) {
                0 ->
                    _dialogUiState.update { it.copy(showDelConfDialog = true) }

                in 1..Int.MAX_VALUE ->
                    _dialogUiState.update { it.copy(showDelNotAllowedDialog = true) }

                -1 ->
                    _messageUiState.update {
                        it.copy(userMessage = R.string.common_unexpected_error)
                    }
            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            val numOfDeleted = directDebitDefRepo.deleteItemBy(id = destId)

            if (numOfDeleted > 0) {
                // 削除に成功した場合

                _somethingUiState.update { it.copy(showDelCompDialog = true) }
            } else {
                // 削除に失敗した場合

                _messageUiState.update { it.copy(userMessage = R.string.common_delete_failed) }
            }
        }
    }

    fun clearMessage() {
        _messageUiState.update {
            it.copy(
                userMessage = null
            )
        }
    }

    fun updateDestInputTypeIndex(index: Int) {
        _somethingUiState.update {
            it.copy(selectedButtonIndex = index)
        }
    }

}
