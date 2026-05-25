package com.kurodai0715.directdebitmanager.ui.screen.payee_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PayeeListScreen(
    viewModel: PayeeListViewModel = hiltViewModel(),
    onClickNavigateUp: () -> Unit,
    onClickAdd: () -> Unit,
    onClickItem: (Int) -> Unit,
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
            items = uiState.items,
            onNavigateUp = onClickNavigateUp,
            onClickAdd = onClickAdd,
            onClickItem = onClickItem,
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }

    }
}

@Composable
fun PayeeListContents(
    modifier: Modifier = Modifier,
    items: List<PayeeUiModel>,
    onNavigateUp: () -> Unit,
    onClickAdd: () -> Unit,
    onClickItem: (Int) -> Unit,
) {

    ContentsWithBottomButton(
        modifier = modifier,
        body = {
            Contents(modifier = Modifier, items = items, onClickItem = onClickItem)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onNavigateUp) },
                onClickRight = { debouncedClick(onClickAdd) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_add)
            )
        }
    )
}

@Composable
fun Contents(
    modifier: Modifier = Modifier,
    items: List<PayeeUiModel>,
    onClickItem: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(LayoutTokens.sectionSpacingHalf),
        verticalArrangement = Arrangement.spacedBy(LayoutTokens.itemSpacing)
    ) {
        items(items) { item ->
            Text(
                text = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .clickable(onClick = { debouncedClick { onClickItem(item.id) } })
                    .padding(LayoutTokens.elementSpacing),
            )
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
            PayeeUiModel(3, "水道料金"),
        ),
        onNavigateUp = {},
        onClickAdd = {},
        onClickItem = {}
    )
}