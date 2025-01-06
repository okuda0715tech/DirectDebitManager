package com.kurodai0715.directdebitmanager.ui.direct_debit_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.appColorScheme
import com.kurodai0715.directdebitmanager.appTypography
import com.kurodai0715.directdebitmanager.data.source.DirectDebit

@Composable
fun DirectDebitListScreen(
    viewModel: DirectDebitListViewModel = hiltViewModel(),
    onNavigateToEdit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn {
            itemsIndexed(uiState.items) { index, item ->
                val modifier = when (index) {
                    // 最初のアイテムは Bottom にのみパディング
                    0 -> Modifier.padding(bottom = 8.dp)
                    // 最後のアイテムは Top にのみパディング
                    uiState.items.size - 1 -> Modifier.padding(top = 8.dp)
                    // それ以外のアイテムは Top と Bottom にパディング
                    else -> Modifier.padding(vertical = 8.dp)
                }
                DirectDebitItem(item, modifier)
            }
        }

        Button(onClick = onNavigateToEdit) {
            Text(stringResource(R.string.common_add))
        }
    }
}

@Composable
fun DirectDebitItem(directDebit: DirectDebit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(appColorScheme.surfaceContainerLow)
            .padding(8.dp)
    ) {
        Text(
            stringResource(R.string.transfer_dest),
            style = appTypography.labelSmall,
            color = appColorScheme.onSurfaceVariant,
        )
        Text(directDebit.destination)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.transfer_source),
            style = appTypography.labelSmall,
            color = appColorScheme.onSurfaceVariant,
        )
        Text(directDebit.source)
    }
}

@Preview
@Composable
private fun Preview() {
    DirectDebitListScreen(onNavigateToEdit = { })
}