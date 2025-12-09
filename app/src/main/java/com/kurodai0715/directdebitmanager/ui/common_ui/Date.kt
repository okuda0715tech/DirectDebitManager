/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kurodai0715.directdebitmanager.R
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.Date
import java.util.Locale

private const val TAG = "Date"

@Composable
fun DatePickerText(onTextChanged: (String) -> Unit) {

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var textFieldValue by remember { mutableStateOf("") }
    var showModal by remember { mutableStateOf(false) }

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                when (interaction) {
                    is PressInteraction.Press -> {
                        Log.d(TAG, "press")
                        showModal = true
                    }
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    TextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        readOnly = true,
        label = { Text(stringResource(R.string.transfer_date)) },
        modifier = Modifier
            .fillMaxWidth(),
        interactionSource = interactionSource,
    )

    selectedDate?.let {
        val date = Date(it)
        textFieldValue = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
    }

    if (showModal) {
        DatePickerModalInput(
            onDateSelected = {
                // コールバックでは、値を状態に設定するだけです。
                // 状態が更新されると再コンポーズが実施されるため、画面を更新することができます。
                selectedDate = it
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // モーダルデートピッカーとの違いはここだけ。
    // initialDisplayMode パラメータに DisplayMode.Input をセットしている点だけです。
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}