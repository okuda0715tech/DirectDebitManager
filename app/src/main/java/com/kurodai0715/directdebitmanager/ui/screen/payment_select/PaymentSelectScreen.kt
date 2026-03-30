package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultListItemFrame
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentSelectScreen(
    viewModel: PaymentSelectViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {

    val uiStateV2 by viewModel.uiStateV2.collectAsStateWithLifecycle()

    PaymentSelectContents(
        uiStateV2 = uiStateV2,
        onClickItem = { viewModel.onClickItem(it) },
        onClickBack = onClickBack,
    )
}

@Composable
fun PaymentSelectContents(
    modifier: Modifier = Modifier,
    uiStateV2: UiStateV2,
    onClickItem: (UiStateV2.Success.Item) -> Unit,
    onClickBack: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(uiStateV2, onClickItem)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick { TODO() } },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_select)
            )
        }
    )
}

@Composable
private fun Contents(
    uiStateV2: UiStateV2,
    onClickItem: (UiStateV2.Success.Item) -> Unit
) {
    when (uiStateV2) {
        is UiStateV2.Error -> {
            // エラー表示
            Text(text = stringResource(uiStateV2.errorMessageRes))
        }

        UiStateV2.Loading -> {
            // ローディング表示
        }

        is UiStateV2.Success -> {
            LazyColumn {
                items(uiStateV2.payments) { item ->
                    ListItem(item, onClickItem = { onClickItem(item) })
                }
            }
        }
    }
}

@Composable
fun ListItem(
    item: UiStateV2.Success.Item,
    onClickItem: () -> Unit
) {
    DefaultListItemFrame(onClickItem = onClickItem) {
        Text(text = item.name)
    }
}

@Preview(name = "PaymentSelectContents")
@Composable
private fun Preview() {
    PaymentSelectContents(
        uiStateV2 = UiStateV2.Success(
            payments = listOf(
                UiStateV2.Success.Item(1, "三井住友銀行", true),
                UiStateV2.Success.Item(2, "リクルートカードプラス", false),
                UiStateV2.Success.Item(3, "横浜銀行", false),
                UiStateV2.Success.Item(4, "楽天銀行", false),
                UiStateV2.Success.Item(5, "電気料金", false),
            ),
        ),
        onClickItem = { },
        onClickBack = { },
    )
}