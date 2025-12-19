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

/**
 * UI で必要とされる全ての状態.
 *
 * 各上流 Flow のデータを加工した結果を状態として保持したい場合はここに定義する。
 */
data class DestinationEditUiState(
    val sourceSelectionDialogItems: List<SourceSelectionUiModel> = emptyList(),
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val uiLocalState: UiLocalState = UiLocalState(),
    val formUiState: FormUiState = FormUiState(),
    val persistedAsyncState: Async<PersistedUiState> = Async.Loading,
)

/**
 * 永続化する必要のない UI の見た目上の状態.
 */
data class UiLocalState(
    val showDelNotAllowedDialog: Boolean = false,
    val showDelConfDialog: Boolean = false,
    val showDelCompDialog: Boolean = false,
    val destErrorMessage: Int? = null,
    val sourceErrorMessage: Int? = null,
    val isLoading: Boolean = false,
    val sourceListDialogType: SourceListDialogType? = null, // TODO 他のダイアログ表示用のプロパティと仕様を統一したいところ。
)

/**
 * 永続化する前の一時的な UI の状態.
 */
data class FormUiState(
    val destIdFromKeyboard: Int = 0,
    val destIdFromDialog: Int? = null,
    val destNameFromKeyboard: String = "",
    val destNameFromDialog: String = "",
    val destItemTypeFromDialog: TransferItemType? = null,
    val sourceId: Int = 0,
    val sourceName: String = "",
    val selectedButton: DestInputType = DestInputType.Keyboard,
)

/**
 * データレイヤーから取得した無加工のデータ.
 */
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
            _uiLocalState,
            _formUiState,
            persistedAsync,
        ) { uiLocalState, formUiState, persistedAsync ->
            when (persistedAsync) {
                is Async.Loading -> {
                    DestinationEditUiState(uiLocalState = UiLocalState(isLoading = true))
                }

                is Async.Error -> {
                    _eventChannel.send(UiEvent.ShowSnackbar(persistedAsync.errorMessage))
                    DestinationEditUiState()
                }

                is Async.Success -> {
                    DestinationEditUiState(
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

    private var initialized = false

    fun initialize(destId: Int?) {
        if (initialized) return
        initialized = true

        if (destId == null) return

        viewModelScope.launch {
            val item = directDebitDefRepo.loadTransferInfo(destId)

            _formUiState.update {
                if (item.inputType == DestInputType.SourceList) {
                    it.copy(
                        destIdFromDialog = item.destId,
                        destNameFromDialog = item.destName,
                        selectedButton = item.inputType,
                        sourceId = item.sourceId,
                        sourceName = item.sourceName,
                    )
                } else {
                    it.copy(
                        destIdFromKeyboard = item.destId,
                        destNameFromKeyboard = item.destName,
                        selectedButton = item.inputType,
                        sourceId = item.sourceId,
                        sourceName = item.sourceName,
                    )
                }
            }
        }
    }

    fun updateDest(dest: String) {
        _formUiState.update {
            it.copy(destNameFromKeyboard = dest)
        }
    }

    private fun getItemBy(destId: Int): TransferItemEntity? {
        val asyncSuccess = persistedAsync.value

        check(asyncSuccess is Async.Success<PersistedUiState>) {
            "persistedAsync must have been retrieved"
        }

        val sources = asyncSuccess.data.sources

        return sources.find { it.id == destId }
    }

    fun updateDestFromDialog(destId: Int) {
        val destination = getItemBy(destId)

        checkNotNull(destination) { "There must be an item matching destId = $destId" }
        checkNotNull(destination.type) { "" }

        _formUiState.update {
            it.copy(
                destIdFromDialog = destId,
                destNameFromDialog = destination.label,
                destItemTypeFromDialog = TransferItemType.fromInt(destination.type)
            )
        }
    }

    fun updateSource(sourceId: Int) {
        val source = getItemBy(sourceId)

        checkNotNull(source) { "There must be an item matching sourceId = $sourceId" }

        _formUiState.update {
            it.copy(
                sourceId = sourceId,
                sourceName = source.label,
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
        _uiLocalState.update {
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
        val destInputType = _formUiState.value.selectedButton

        return when (destInputType) {
            DestInputType.Keyboard -> _formUiState.value.destNameFromKeyboard
            DestInputType.SourceList -> _formUiState.value.destNameFromDialog
        }
    }

    val destId: Int?
        get() {
            val destInputType = _formUiState.value.selectedButton

            return when (destInputType) {
                DestInputType.Keyboard -> _formUiState.value.destIdFromKeyboard
                DestInputType.SourceList -> _formUiState.value.destIdFromDialog
            }
        }

    private fun sourceValidation(): Boolean {
        val validationResult = BasicTextValidator.validate(_formUiState.value.sourceName)
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
            val isSourceItem = _formUiState.value.selectedButton == DestInputType.SourceList
            val itemType = if (isSourceItem) _formUiState.value.destItemTypeFromDialog else null

            val resultSuccess = directDebitDefRepo.upsertDestination(
                id = destId,
                label = getDestName(),
                isSourceItem = isSourceItem,
                parentId = _formUiState.value.sourceId,
                type = itemType,
            )

            if (resultSuccess) {
                // 新規作成 or 更新が成功した場合
                if (destId == 0) {
                    // 新規作成の場合
                    // 入力フォームを初期化
                    _formUiState.update { FormUiState() }
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

    fun updateDestInputType(type: DestInputType) {
        _formUiState.update {
            it.copy(selectedButton = type)
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
