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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "DestinationEditViewModel.kt"

data class DestinationEditUiState(
    val sourceName: String = "",
    val sources: List<SourceUiModel> = emptyList(),
    val sourceSelectionDialogItems: List<SourceSelectionUiModel> = emptyList(),
    val destInputType: DestInputType = DestInputType.Keyboard, // TODO 未使用なので削除するべき？使うべき？
    val selectedButtonIndex: Int = 0, // セグメントボタンの選択されたボタンのインデックス
    val destInputTypes: List<DestInputType> = DestInputType.getSortedList(),
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val sourceListDialogType: SourceListDialogType? = null,
    val uiLocalState: UiLocalState = UiLocalState(),
    val formUiState: FormUiState = FormUiState(),
    val persistedAsyncState: Async<PersistedUiState> = Async.Loading,
)

data class UiLocalState(
    val showDelNotAllowedDialog: Boolean = false,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val destErrorMessage: Int? = null,
    val sourceErrorMessage: Int? = null,
    val isLoading: Boolean = false,
)

data class FormUiState(
    val destIdFromKeyboard: Int = 0,
    val destIdFromDialog: Int? = null,
    val destNameFromKeyboard: String = "",
    val destNameFromDialog: String = "",
    val sourceId: Int = 0,
)

data class PersistedUiState(
    val sources: List<TransferItemEntity> = emptyList(),
)

sealed class UiEvent {
    data class ShowSnackbar(val messageRes: Int) : UiEvent()

    object NavigateUp : UiEvent()
    object NavigateToSourceList : UiEvent()
    object NavigateToSourceEdit : UiEvent()
}

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
    private val _uiLocalState = MutableStateFlow(UiLocalState())

    /**
     * 更新用.
     */
    private val _formUiState = MutableStateFlow(FormUiState())

    private val persistedAsync: StateFlow<Async<PersistedUiState>> =
        // Repository から複数の Flow を取得する場合はここを combine にすれば OK
        directDebitDefRepo.loadSourcesStream()
            .map { sources ->
                PersistedUiState(
                    sources = sources
                )
            }
            .map<PersistedUiState, Async<PersistedUiState>> { state ->
                Async.Success(state)
            }
            .catch {
                Log.e(TAG, "Failed to read trans sources.", it)
                emit(Async.Error(R.string.load_error))
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                Async.Loading
            )

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<DestinationEditUiState> =
        combine(
            _somethingUiState,
            _uiLocalState,
            _formUiState,
            persistedAsync,
        ) { uiState, uiLocalState, formUiState, persistedAsync ->
            when (persistedAsync) {
                is Async.Loading -> {
                    DestinationEditUiState(uiLocalState = UiLocalState(isLoading = true))
                }

                is Async.Error -> {
                    _eventChannel.send(UiEvent.ShowSnackbar(persistedAsync.errorMessage))
                    DestinationEditUiState()
                }

                is Async.Success -> {
                    val sourceUiModels = persistedAsync.data.sources.map { it.toSourceUiModel() }
                    DestinationEditUiState(
//                        destNameFromKeyboard = uiState.destNameFromKeyboard,
//                        destNameFromDialog = uiState.destNameFromDialog,
//                        sourceId = uiState.sourceId,
                        destInputType = uiState.destInputType,
                        selectedButtonIndex = uiState.selectedButtonIndex,
                        destInputTypes = uiState.destInputTypes,
                        sourceListDialogType = uiState.sourceListDialogType,
                        sourceName = getSourceString(
                            sourceId = formUiState.sourceId,
                            sources = sourceUiModels
                        ),
                        sources = sourceUiModels,
                        sourceSelectionDialogItems = persistedAsync.data.sources.toSourceSelectionUiModel(),
                        uiLocalState = uiLocalState.copy(isLoading = false),
                        formUiState = formUiState,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = DestinationEditUiState(uiLocalState = UiLocalState(isLoading = true))
        )

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<UiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()

    fun initialize(destId: Int?) {
        viewModelScope.launch {
            if (destId != null) {
                val item = directDebitDefRepo.loadTransferItem(destId)

                _somethingUiState.update {
                    if (item.destination.isSourceItem) {
                        it.copy(
                            selectedButtonIndex = 1,
                            sourceName = item.sourceName,
                        )
                    } else {
                        it.copy(
                            selectedButtonIndex = 0,
                            sourceName = item.sourceName,
                        )
                    }
                }

                _formUiState.update {
                    if (item.destination.isSourceItem) {
                        it.copy(
                            destIdFromDialog = item.destination.id,
                            destNameFromDialog = item.destination.label,
                            sourceId = item.destination.parentId,
                        )
                    } else {
                        it.copy(
                            destIdFromKeyboard = item.destination.id,
                            destNameFromKeyboard = item.destination.label,
                            sourceId = item.destination.parentId,
                        )
                    }
                }
            }
        }
    }

    private fun getSourceString(sourceId: Int, sources: List<SourceUiModel>): String {
        for (source in sources) {
            if (source.id == sourceId) {
                return source.name
            }
        }
        return ""
    }

    fun updateDest(dest: String) {
        _formUiState.update {
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

        _formUiState.update {
            it.copy(
                destIdFromDialog = destId,
                destNameFromDialog = source.name,
            )
        }
    }

    fun updateSource(sourceId: Int) {
        _formUiState.update {
            it.copy(
                sourceId = sourceId,
            )
        }
    }

    fun updateDelNotAllowedDialogVisibility(show: Boolean) {
        _uiLocalState.update {
            it.copy(showDelNotAllowedDialog = show)
        }
    }

    fun updateDelConfDialogVisibility(show: Boolean) {
        _uiLocalState.update {
            it.copy(showDelConfDialog = show)
        }
    }

    fun updateDelCompDialogVisibility(show: Boolean) {
        _uiLocalState.update {
            it.copy(showDelCompDialog = show)
        }
    }

    fun updateSourceListDialogType(type: SourceListDialogType?) {
        _somethingUiState.update {
            it.copy(sourceListDialogType = type)
        }
    }

    fun updateDestErrorMessage(message: Int?) {
        _uiLocalState.update {
            it.copy(
                destErrorMessage = message
            )
        }
    }

    fun updateSourceErrorMessage(message: Int?) {
        _uiLocalState.update {
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
            DestInputType.Keyboard.defaultDisplayOrder -> uiState.value.formUiState.destNameFromKeyboard
            DestInputType.SourceList.defaultDisplayOrder -> uiState.value.formUiState.destNameFromDialog
            else -> throw IllegalStateException("Unexpected value: $selectedButtonIndex")
        }
    }

    val destId: Int?
        get() {
            val destInputTypeIndex = uiState.value.selectedButtonIndex

            return when (destInputTypeIndex) {
                DestInputType.Keyboard.defaultDisplayOrder -> uiState.value.formUiState.destIdFromKeyboard
                DestInputType.SourceList.defaultDisplayOrder -> uiState.value.formUiState.destIdFromDialog
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
            val isSourceItem =
                uiState.value.selectedButtonIndex == DestInputType.SourceList.defaultDisplayOrder
            val itemType = if (isSourceItem) TransferItemType.fromInt(destId!!) else null

            val resultSuccess = directDebitDefRepo.upsertDestination(
                id = destId,
                label = getDestName(),
                isSourceItem = isSourceItem,
                parentId = uiState.value.formUiState.sourceId,
                type = itemType,
            )

            if (resultSuccess) {
                // 新規作成 or 更新が成功した場合
                if (destId == 0) {
                    // 新規作成の場合
                    _formUiState.update {
                        it.copy(
                            destNameFromKeyboard = "",
                            destNameFromDialog = "",
                            sourceId = 0,
                        )
                    }
                }
            }

            if (resultSuccess) {
                // 新規作成 or 更新が成功した場合
                if (destId == 0) {
                    // 新規作成の場合
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_register_successfully))
                } else {
                    // 更新の場合
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_update_successfully))
                }
            } else {
                // 新規作成 or 更新が失敗した場合
                _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    fun checkRelatedDataExistence() {
        viewModelScope.launch {
            // destinationId を振替元として使用している振替先データの件数
            val relatedDestCount = directDebitDefRepo.countDestinationsReferencing(destId)

            when (relatedDestCount) {
                0 ->
                    _uiLocalState.update {
                        it.copy(showDelConfDialog = true)
                    }

                in 1..Int.MAX_VALUE ->
                    _uiLocalState.update {
                        it.copy(showDelNotAllowedDialog = true)
                    }

                -1 -> {
                    _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_unexpected_error))
                }

            }
        }
    }

    fun deleteData() {
        viewModelScope.launch {
            val numOfDeleted = directDebitDefRepo.deleteItemBy(id = destId)

            if (numOfDeleted > 0) {
                // 削除に成功した場合

                _uiLocalState.update {
                    it.copy(showDelCompDialog = true)
                }
            } else {
                // 削除に失敗した場合

                _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_delete_failed))
            }
        }
    }

    fun updateDestInputTypeIndex(index: Int) {
        _somethingUiState.update {
            it.copy(selectedButtonIndex = index)
        }
    }

    fun requestNavigateUp() {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.NavigateUp)
        }
    }

    fun requestNavigateToSourceList() {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.NavigateToSourceList)
        }
    }

    fun requestNavigateToSourceEdit() {
        viewModelScope.launch {
            _eventChannel.send(UiEvent.NavigateToSourceEdit)
        }
    }
}
