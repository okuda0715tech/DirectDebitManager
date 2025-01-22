package com.kurodai0715.directdebitmanager.ui.source_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.TransSource
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceListScreen(
    onNavigateToEdit: (TransSource?) -> Unit,
) {
    SourceListContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF),
        onNavigateToEdit = onNavigateToEdit
    )
}

@Composable
fun SourceListContents(
    modifier: Modifier = Modifier,
    onNavigateToEdit: (TransSource?) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Button(onClick = { debouncedClick { onNavigateToEdit(null) } }) {
            Text(stringResource(R.string.common_add))
        }
    }
}
