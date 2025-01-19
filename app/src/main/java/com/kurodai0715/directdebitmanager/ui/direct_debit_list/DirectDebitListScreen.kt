package com.kurodai0715.directdebitmanager.ui.direct_debit_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import com.kurodai0715.directdebitmanager.ui.component.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "DirectDebitListScreen.kt"

@Composable
fun DirectDebitListScreen(
    viewModel: DirectDebitListViewModel = hiltViewModel(),
    onNavigateToEdit: (DirectDebit?) -> Unit,
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

        ListScreenContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            items = uiState.items,
            onNavigateToEdit = onNavigateToEdit
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }

    }
}

@Composable
fun ListScreenContents(
    modifier: Modifier = Modifier,
    items: List<DirectDebit>,
    onNavigateToEdit: (DirectDebit?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(items) { index, item ->
                val itemModifier = when (index) {
                    // 最初のアイテムは Bottom にのみパディング
                    0 -> Modifier.padding(bottom = 8.dp)
                    // 最後のアイテムは Top にのみパディング
                    items.size - 1 -> Modifier.padding(top = 8.dp)
                    // それ以外のアイテムは Top と Bottom にパディング
                    else -> Modifier.padding(vertical = 8.dp)
                }
                DirectDebitItem(item, itemModifier, onClickItem = { onNavigateToEdit(item) })
            }
        }

        Button(onClick = { debouncedClick { onNavigateToEdit(null) } }) {
            Text(stringResource(R.string.common_add))
        }
    }
}

@Composable
fun DirectDebitItem(
    directDebit: DirectDebit,
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = {
                Log.v(TAG, "list item is clicked.")
                debouncedClick(onClickItem)
            })
            .padding(8.dp)
    ) {
        Text(
            stringResource(R.string.transfer_dest),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(directDebit.destination)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.transfer_source),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(directDebit.source)
    }
}

@Preview
@Composable
private fun Preview() {
    ListScreenContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF),
        items = listOf(
            DirectDebit(1, "横浜銀行クレジットカード", "横浜銀行"),
            DirectDebit(2, "Oliveクレジットカード", "三井住友銀行")
        ),
        onNavigateToEdit = { })
}