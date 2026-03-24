package com.kurodai0715.directdebitmanager.ui.screen.payee_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.BasicTextValidator
import com.kurodai0715.directdebitmanager.domain.ValidationResult
import com.kurodai0715.directdebitmanager.domain.model.Payee
import com.kurodai0715.directdebitmanager.domain.model.SaveResult
import com.kurodai0715.directdebitmanager.domain.usecase.PayeeCommandUseCase
import com.kurodai0715.directdebitmanager.domain.usecase.PayeeQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "RegisterPayeeViewModel.kt"

data class PayeeEditUiState(
    val editMode: PayeeEditMode = PayeeEditMode.Add,
    val payeeName: String = "",
    val payeeErrorMessage: Int? = null,
)

sealed class RegisterPayeeUiEvent {
    data class ShowSnackbar(val messageRes: Int) : RegisterPayeeUiEvent()
}

@HiltViewModel
class PayeeEditViewModel @Inject constructor(
    private val payeeQueryUseCase: PayeeQueryUseCase,
    private val payeeCommandUseCase: PayeeCommandUseCase,
) : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(PayeeEditUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<PayeeEditUiState> = _uiState.asStateFlow()

    /**
     * 更新用.
     */
    private val _eventChannel = Channel<RegisterPayeeUiEvent>(Channel.BUFFERED)

    /**
     * 参照用.
     */
    val eventFlow = _eventChannel.receiveAsFlow()

    private var initialized = false

    fun initialize(payeeId: Int?) {
        if (initialized) return
        initialized = true

        payeeId?.let { loadPayeeBy(it) }
    }

    private fun loadPayeeBy(payeeId: Int) {
        viewModelScope.launch {
            val item = payeeQueryUseCase.loadPayeeBy(payeeId)

            _uiState.update {
                it.copy(
                    editMode = PayeeEditMode.Edit(item.id),
                    payeeName = item.name.value
                )
            }
        }
    }

    fun updatePayeeName(payeeName: String) {
        _uiState.update {
            it.copy(payeeName = payeeName)
        }
    }

    fun validate() {
        val sourceValidationSuccess = sourceValidation()

        if (!sourceValidationSuccess) return

        saveData()
    }

    private fun sourceValidation(): Boolean {
        val validationResult = BasicTextValidator.validate(uiState.value.payeeName)
        val message = when (validationResult) {
            ValidationResult.EmptyError -> R.string.common_required_field
            ValidationResult.LengthWithin100Error -> R.string.common_length_needs_to_be_within_100
            else -> null
        }

        updatePayeeNameMessage(message)

        return validationResult == ValidationResult.Valid
    }

    private fun updatePayeeNameMessage(message: Int?) {
        _uiState.update {
            it.copy(
                payeeErrorMessage = message
            )
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            val payee = uiState.value.toDomain()

            val result = savePayee(payee)

            when (result) {
                SaveResult.Succeeded -> {
                    _eventChannel.send(RegisterPayeeUiEvent.ShowSnackbar(R.string.common_save_successfully))
                }

                SaveResult.Failed ->
                    _eventChannel.send(RegisterPayeeUiEvent.ShowSnackbar(R.string.common_save_failed))
            }
        }
    }

    private suspend fun savePayee(payee: Payee): SaveResult {
        return payeeCommandUseCase.savePayee(payee)
    }
}