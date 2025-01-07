package com.kurodai0715.directdebitmanager.ui.app_base

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.navigation.AppNavGraph
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBaseScreen() {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedMenuItem by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                selectedIndex = selectedMenuItem,
                items = stringArrayResource(R.array.menu_items),
                onClickItem = { index ->
                    selectedMenuItem = index
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
                onChangeTitle = { title = it }
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
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickMenu) {
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
fun AppDrawerContent(selectedIndex: Int, items: Array<String>, onClickItem: (Int) -> Unit) {
    ModalDrawerSheet {
        Text(stringResource(R.string.screen_list), modifier = Modifier.padding(16.dp))
        HorizontalDivider()

        for ((index, item) in items.withIndex()) {
            NavigationDrawerItem(
                label = { Text(text = item) },
                selected = index == selectedIndex,
                onClick = { onClickItem(index) }
            )
        }
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
                selectedIndex = 0,
                items = stringArrayResource(R.array.menu_items),
                onClickItem = {},
            )
        },
        drawerState = drawerState
    ) {}
}
