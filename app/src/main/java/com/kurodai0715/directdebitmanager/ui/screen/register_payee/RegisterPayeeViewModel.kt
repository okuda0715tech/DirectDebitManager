package com.kurodai0715.directdebitmanager.ui.screen.register_payee

import androidx.lifecycle.ViewModel
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.BasicTextValidator
import com.kurodai0715.directdebitmanager.domain.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RegisterPayeeUiState(
    val id: Int = 0,
    val payeeName: String = "",
    val payeeErrorMessage: Int? = null,
)

@HiltViewModel
class RegisterPayeeViewModel @Inject constructor() : ViewModel() {

    /**
     * 更新用.
     */
    private val _uiState = MutableStateFlow(RegisterPayeeUiState())

    /**
     * 読み取り専用.
     */
    val uiState: StateFlow<RegisterPayeeUiState> = _uiState.asStateFlow()

    fun updatePayeeName(payeeName: String) {
        _uiState.update {
            it.copy(payeeName = payeeName)
        }
    }

    fun validate() {
        val sourceValidationSuccess = sourceValidation()

        if (!sourceValidationSuccess) return

//        saveData()
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

    private fun updatePayeeNameMessage(message: Int?){
        _uiState.update {
            it.copy(
                payeeErrorMessage = message
            )
        }
    }

    private fun saveData() {
        TODO()
    }
}