package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.component.DatePickerText
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF

@Composable
fun EditDirectDebitScreen(
    viewModel: EditDirectDebitViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF)
    ) {
        TextField(
            value = uiState.transferDest,
            onValueChange = { viewModel.updateDest(it) },
            label = { Text(stringResource(R.string.transfer_dest)) },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = uiState.transferSource,
            onValueChange = { viewModel.updateSource(it) },
            label = { Text(stringResource(R.string.transfer_source)) },
            modifier = Modifier.fillMaxWidth(),
        )
//        DatePickerText(onTextChanged = {
//            viewModel.updateDate(it)
//        })
//        TextField(
//            value = uiState.transferAmount.toString(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            onValueChange = { viewModel.updateAmount(it) },
//            label = { Text(stringResource(R.string.transfer_amount)) },
//            modifier = Modifier.fillMaxWidth(),
//        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = { viewModel.saveData() }) {
                Text(stringResource(R.string.common_save))
            }
        }
    }
}