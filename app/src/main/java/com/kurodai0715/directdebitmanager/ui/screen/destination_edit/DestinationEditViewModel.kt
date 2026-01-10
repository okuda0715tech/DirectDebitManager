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
import com.kurodai0715.directdebitmanager.domain.model.ItemType
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
//    val transferDate: String = "",
//    val transferAmount: String = "",
    val uiLocalState: UiLocalState = UiLocalState(),
    val formUiState: FormUiState = FormUiState(),
    val persistedDataState: PersistedDataState = PersistedDataState(
        sourceUiModels = emptyList(),
    ),
)

/**
 * 永続化の対象外の UI 状態.
 *
 * 主に UI の見た目にのみ関わる状態を管理する。
 */
data class UiLocalState(
    val destErrorMessage: Int? = null,
    val sourceErrorMessage: Int? = null,
    val isLoading: Boolean = false,
    val dialog: DestinationEditDialog? = null,
)

/**
 * 永続化の対象の UI 状態.
 */
data class FormUiState(
    val sourceId: Int = 0,
    val sourceName: String = "",
    val inputType: DestInputType = DestInputType.Keyboard,
    val destInput: DestInput = DestInput.New(destId = 0, name = ""),
)

/**
 * データレイヤーから取得したデータ.
 */
data class PersistedDataState(
    val sourceUiModels: List<SourceSelectionUiModel>
)

sealed interface DestinationEditDialog {
    data object DeleteNotAllowed : DestinationEditDialog
    data object DeleteConfirm : DestinationEditDialog
    data object DeleteCompletion : DestinationEditDialog

    data class SourceSelection(val type: TargetType) : DestinationEditDialog
    data object NoSourceData : DestinationEditDialog
}

sealed interface TargetType {
    data object Source : TargetType
    data object Destination : TargetType
}

sealed interface DestInput {
    val destId: Int?
    val name: String

    data class New(
        override val destId: Int,
        override val name: String
    ) : DestInput

    data class Existing(
        override val destId: Int?,
        override val name: String,
        val type: ItemType?
    ) : DestInput
}


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

    private val _uiLocalState = MutableStateFlow(UiLocalState())

    private val _formUiState = MutableStateFlow(FormUiState())

    private var sourceIndexedCache = emptyMap<Int, TransferItemEntity>()

    // Repository から複数の Flow を取得する場合は map() を combine() にすれば OK
    //
    // 【例】
    //
    //  private val accountsFlow: Flow<List<Account>> =
    //      directDebitDefRepo.loadAccountsStream()
    //
    //  private val persistedAsync: StateFlow<Async<PersistedDataState>> =
    //      combine(
    //          observeSourcesSideEffect(),
    //          accountsFlow
    //      ) { sources, accounts ->
    //          PersistedDataState(
    //              sourceLookup = SourceLookupState(
    //                  sourceUiModels = sources.toSourceSelectionUiModel(),
    //                  sourceIndex = sources.associateBy { it.id }
    //              ),
    //              accountLookup = AccountLookupState(
    //                  accountUiModels = accounts.toUiModels(),
    //                  accountIndex = accounts.associateBy { it.id }
    //              )
    //          )
    //      }
    private val persistedAsync: StateFlow<Async<PersistedDataState>> =
        directDebitDefRepo.loadSourcesStream()
            .map { sources ->
                PersistedDataState(
                    sourceUiModels = sources.toSourceSelectionUiModel(),
                )
            }
            .map<PersistedDataState, Async<PersistedDataState>> { state ->
                Async.Success(state)
            }
            .catch {
                Log.e(TAG, "Failed to read trans sources.", it)
                emit(Async.Error(R.string.load_error))
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = Async.Loading
            )

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
                        uiLocalState = uiLocalState.copy(isLoading = false),
                        formUiState = formUiState,
                        persistedDataState = persistedAsync.data
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

        destId?.let { loadInitialDest(it) }
        observeSources()
    }

    private fun loadInitialDest(destId: Int) {
        viewModelScope.launch {
            val item = directDebitDefRepo.loadTransferInfo(destId)

            val destInput = if (item.inputType == DestInputType.SourceList) {
                item.toDestInputSourceList()
            } else {
                item.toDestInputKeyboard()
            }

            _formUiState.update {
                it.copy(
                    destInput = destInput,
                    inputType = item.inputType,
                    sourceId = item.sourceId,
                    sourceName = item.sourceName,
                )
            }
        }
    }

    private fun observeSources() {
        directDebitDefRepo.loadSourcesStream()
            .onEach { sources ->
                sourceIndexedCache = sources.associateBy(TransferItemEntity::id)
            }
            .launchIn(viewModelScope)
    }

    fun updateDest(dest: String) {
        _formUiState.update {
            val destId = it.destInput.destId
            checkNotNull(destId) { "destId must not be null" }

            it.copy(
                destInput = DestInput.New(
                    destId = destId,
                    name = dest
                )
            )
        }
    }

    private fun getItemBy(destId: Int): TransferItemEntity? {
        val asyncSuccess = persistedAsync.value

        check(asyncSuccess is Async.Success<PersistedDataState>) {
            "persistedAsync must have been retrieved"
        }

        return sourceIndexedCache[destId]
    }

    fun updateDestFromDialog(destId: Int) {
        val destination = getItemBy(destId)

        checkNotNull(destination) { "There must be an item matching destId = $destId" }
        checkNotNull(destination.type) { "" }

        _formUiState.update {
            it.copy(
                destInput = DestInput.Existing(
                    destId = destId,
                    name = destination.label,
                    type = ItemType.fromInt(destination.type)
                )
            )
        }
    }

    // TODO ダイアログから選択した時しか画面に表示される振替先が更新されないため、
    //  振替元編集画面から振替元の名前が変更された場合にも、
    //  振替先編集画面の振替元の名前が変わるようにする。
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

    fun updateDialogState(dialogState: DestinationEditDialog?) {
        _uiLocalState.update {
            it.copy(dialog = dialogState)
        }
    }

    private fun getSourceUiModels(): List<SourceSelectionUiModel> {
        return when (val async = persistedAsync.value) {
            is Async.Success<PersistedDataState> ->
                async.data.sourceUiModels

            else -> emptyList()
        }

    }

    fun onClickSource() {
        if (getSourceUiModels().isEmpty()) {
            updateDialogState(DestinationEditDialog.NoSourceData)
        } else {
            updateDialogState(DestinationEditDialog.SourceSelection(TargetType.Source))
        }
    }

    fun onClickDestSelectField() {
        if (getSourceUiModels().isEmpty()) {
            updateDialogState(DestinationEditDialog.NoSourceData)
        } else {
            updateDialogState(DestinationEditDialog.SourceSelection(TargetType.Destination))
        }
    }

    fun onClickSourceItem(type: TargetType, itemId: Int) {
        when (type) {
            TargetType.Source -> updateSource(itemId)
            TargetType.Destination -> updateDestFromDialog(itemId)
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

    private fun getDestName(): String = _formUiState.value.destInput.name

    val destId: Int?
        get() {
            // 【注意】 getter は必要です。
            // getter を使わずに直接代入してしまうと、 Int 型なので、データのコピーが保存されるだけになってしまう。
            return _formUiState.value.destInput.destId
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
            val isExistingItem = _formUiState.value.destInput is DestInput.Existing
            val type = (_formUiState.value.destInput as? DestInput.Existing)?.type

            val resultSuccess = directDebitDefRepo.upsertDestination(
                id = destId,
                label = getDestName(),
                isSourceItem = isExistingItem,
                parentId = _formUiState.value.sourceId,
                type = type,
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
                        it.copy(dialog = DestinationEditDialog.DeleteConfirm)
                    }

                in 1..Int.MAX_VALUE ->
                    _uiLocalState.update {
                        it.copy(dialog = DestinationEditDialog.DeleteNotAllowed)
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
                    it.copy(dialog = DestinationEditDialog.DeleteCompletion)
                }
            } else {
                // 削除に失敗した場合

                _eventChannel.send(UiEvent.ShowSnackbar(R.string.common_delete_failed))
            }
        }
    }

    fun updateDestInputType(type: DestInputType) {
        _formUiState.update {
            it.copy(inputType = type)
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
