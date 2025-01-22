package com.kurodai0715.directdebitmanager.ui.source_edit

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.TransSource
import com.kurodai0715.directdebitmanager.ui.edit_direct_debit.TAG
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceEditScreen(
    viewModel: SourceEditViewModel = hiltViewModel(),
    transSource: TransSource?,
    onNavigateUp: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState,
            // Snackbar がキーボードで隠れないようにする。
            modifier = Modifier.safeDrawingPadding()
        )
    }) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // リスト画面から引き継いだパラメータで UI 状態を初期化する。
        LaunchedEffect(transSource) {
            if (transSource != null) {
                viewModel.updateTransSource(transSource)
            }
        }

        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.clearMessage()
            }
        }

        SourceEditContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            source = uiState.source,
            onSourceChanged = { viewModel.updateSource(it) },
            itemId = uiState.id,
            onClickDelete = { TODO() },
            onNavigateUp = onNavigateUp,
            onClickSave = { viewModel.saveData() },
        )
    }
}

@Composable
fun SourceEditContents(
    modifier: Modifier = Modifier,
    source: String,
    onSourceChanged: (String) -> Unit,
    itemId: Int,
    onClickDelete: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickSave: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = source,
            onValueChange = onSourceChanged,
            label = { Text(stringResource(R.string.transfer_source)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (itemId != 0) {
                TextButton(onClick = { debouncedClick(onClickDelete) }) {
                    Text(
                        text = stringResource(R.string.common_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            OutlinedButton(onClick = {
                Log.v(TAG, "back is clicked.")
                debouncedClick(onNavigateUp)
            }) {
                Text(stringResource(R.string.common_back))
            }
            Button(onClick = { debouncedClick(onClickSave) }) {
                Text(stringResource(R.string.common_save))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRegisterContents() {
    SourceEditContents(
        source = "横浜銀行",
        onSourceChanged = {},
        itemId = 0,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
    )
}

@Preview
@Composable
private fun PreviewUpdateContents() {
    SourceEditContents(
        source = "横浜銀行",
        onSourceChanged = {},
        itemId = 1,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
    )
}
