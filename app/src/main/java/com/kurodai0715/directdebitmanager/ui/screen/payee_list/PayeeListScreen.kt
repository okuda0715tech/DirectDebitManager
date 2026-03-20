package com.kurodai0715.directdebitmanager.ui.screen.payee_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens

@Composable
fun PayeeListScreen(modifier: Modifier = Modifier) {
    PayeeListContents(modifier)
}

@Composable
fun PayeeListContents(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(LayoutTokens.sectionSpacingHalf),
        verticalArrangement = Arrangement.spacedBy(LayoutTokens.itemSpacing)
    ) {
        items(listOf("xxx", "yyy", "zzz")) { item ->
            Text(text = item)
        }
    }
}

@Preview
@Composable
private fun PreviewPayeeListContents() {
    PayeeListContents()
}