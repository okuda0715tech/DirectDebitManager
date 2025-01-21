package com.kurodai0715.directdebitmanager.ui.app_base

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.animation.LABEL_APP_BAR_TITLE
import com.kurodai0715.directdebitmanager.ui.navigation.AppNavGraph
import com.kurodai0715.directdebitmanager.ui.navigation.BankList
import com.kurodai0715.directdebitmanager.ui.navigation.List
import com.kurodai0715.directdebitmanager.ui.navigation.PrivacyPolicy
import com.kurodai0715.directdebitmanager.ui.theme.ICON_DEF_SIZE
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

private const val TAG = "AppBaseScreen.kt"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBaseScreen() {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem: Any by remember { mutableStateOf(List) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                selectedItem = selectedDrawerItem,
                onClickItem = { selectedItem ->
                    selectedDrawerItem = selectedItem
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        },
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        @StringRes var title by remember { mutableIntStateOf(R.string.no_title) }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppTopBar(
                    scrollBehavior = scrollBehavior,
                    onClickMenu = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    title = stringResource(title),
                )
            }
        ) { contentPadding ->
            AppNavGraph(
                modifier = Modifier
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
                onChangeTitle = { title = it },
                startDestination = selectedDrawerItem,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onClickMenu: () -> Unit,
    title: String,
    // TODO アイコンリソースをパラメータで渡して、戻るアイコンも表示できるようにする
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            AnimatedContent(targetState = title, label = LABEL_APP_BAR_TITLE) { targetState ->
                Text(
                    text = targetState,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { debouncedClick(onClickMenu) }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_menu_24),
                    contentDescription = stringResource(R.string.menu)
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun AppDrawerContent(
    selectedItem: Any,
    onClickItem: (Any) -> Unit
) {
    ModalDrawerSheet {
        Text(stringResource(R.string.screen_list), modifier = Modifier.padding(16.dp))
        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.direct_debit_info)) },
            selected = selectedItem is List,
            onClick = { debouncedClick { onClickItem(List) } }
        )

        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.bank_info)) },
            selected = selectedItem is BankList,
            onClick = { debouncedClick { onClickItem(BankList) } }
        )

        NavigationDrawerItem(
            label = { Text(text = stringResource(R.string.privacy_policy)) },
            selected = false,
            onClick = { debouncedClick { TODO() } },
            badge = {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_outward_24),
                    contentDescription = stringResource(R.string.open_in_outer_browser_icon_description),
                    modifier = Modifier.size(ICON_DEF_SIZE)
                )
            }
        )
    }
}

@Preview
@Composable
fun Preview() {
    AppBaseScreen()
}

@Preview
@Composable
fun PreviewOpenedAppDrawer() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open) // ドロワーを開いた状態に設定

    ModalNavigationDrawer(
        drawerContent = {
            AppDrawerContent(
                selectedItem = List,
                onClickItem = {},
            )
        },
        drawerState = drawerState
    ) {}
}
