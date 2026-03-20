package com.kurodai0715.directdebitmanager.ui.screen.payee_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens

@Composable
fun PayeeListScreen(
    modifier: Modifier = Modifier,
    viewModel: PayeeListViewModel = hiltViewModel(),
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

        PayeeListContents(
            modifier = Modifier.padding(paddingValues),
            items = uiState.items
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }

    }
}

@Composable
fun PayeeListContents(
    modifier: Modifier = Modifier,
    items: List<PayeeUiModel>
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(LayoutTokens.sectionSpacingHalf),
        verticalArrangement = Arrangement.spacedBy(LayoutTokens.itemSpacing)
    ) {
        items(items) { item ->
            Text(text = item.name)
        }
    }
}

@Preview
@Composable
private fun PreviewPayeeListContents() {
    PayeeListContents(
        items = listOf(
            PayeeUiModel(1, "横浜銀行クレジットカード"),
            PayeeUiModel(2, "電気料金"),
        )
    )
}