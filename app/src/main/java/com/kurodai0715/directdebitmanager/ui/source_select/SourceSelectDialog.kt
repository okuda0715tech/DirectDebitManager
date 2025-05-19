package com.kurodai0715.directdebitmanager.ui.source_select

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.TransSource
import com.kurodai0715.directdebitmanager.ui.component.SurfaceButton
import com.kurodai0715.directdebitmanager.ui.theme.ICON_EX_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceSelectDialog(
    modifier: Modifier = Modifier,
    viewModel: SourceSelectViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit,
    onClickItem: (Int) -> Unit,
    onClickEdit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.loading) {
        if (uiState.sources.isNotEmpty()) {
            SourceSelectContents(
                modifier = modifier,
                sources = uiState.sources,
                onDismissRequest = onDismissRequest,
                onClickItem = onClickItem,
                onClickEdit = onClickEdit,
            )
        } else {
            NoDataContents(onDismissRequest = {})
        }
    }
}

@Composable
fun SourceSelectContents(
    modifier: Modifier = Modifier,
    sources: List<TransSource>,
    onDismissRequest: () -> Unit,
    onClickItem: (Int) -> Unit,
    onClickEdit: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LazyColumn(modifier = Modifier.padding(SCREEN_EDGE_PADDING_DEF)) {
                    itemsIndexed(sources) { index, item ->

                        SurfaceButton(
                            onClick = {
                                debouncedClick {
                                    onClickItem(index)
                                    onDismissRequest()
                                }
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            Text(
                                text = item.source,
                                modifier = modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .padding(12.dp)
                            )
                        }

                        // 最後のアイテム以外なら分割線を引く
                        if (index != sources.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }

                HorizontalDivider()

                OutlinedButton(onClick = {
                    debouncedClick {
                        onDismissRequest()
                        onClickEdit()
                    }
                }) {
                    Text(stringResource(R.string.common_edit))
                }
            }
        }
    }
}

@Composable
fun NoDataContents(
    onDismissRequest: () -> Unit,
) {

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_outline_24),
                contentDescription = stringResource(id = R.string.usage_rules_icon_description),
                modifier = Modifier.size(ICON_EX_LARGE_SIZE),
            )
        },
        title = {
            Text(text = stringResource(R.string.no_source_data_title))
        },
        text = {
            Text(text = stringResource(R.string.no_source_data_text))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                debouncedClick {
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_close))
            }
        },
    )
}

@Preview
@Composable
private fun PreviewSourceSelectContents() {
    SourceSelectContents(
        sources = listOf(
            TransSource(id = 1, source = "横浜銀行"),
            TransSource(id = 2, source = "三井住友銀行"),
            TransSource(id = 3, source = "PayPay銀行"),
        ),
        onDismissRequest = {},
        onClickItem = {},
        onClickEdit = {},
    )
}

@Preview
@Composable
private fun PreviewNoDataContents() {
    NoDataContents { }
}