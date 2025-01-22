package com.kurodai0715.directdebitmanager.ui.source_list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.TransSource
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceListScreen(
    onNavigateToEdit: (TransSource?) -> Unit,
) {
    SourceListContents(onNavigateToEdit = onNavigateToEdit)
}

@Composable
fun SourceListContents(
    onNavigateToEdit: (TransSource?) -> Unit,
) {
    Column {
        Button(onClick = { debouncedClick { onNavigateToEdit(null) } }) {
            Text(stringResource(R.string.common_add))
        }
    }
}
