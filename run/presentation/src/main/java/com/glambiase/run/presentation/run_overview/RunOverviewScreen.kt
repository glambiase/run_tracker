@file:OptIn(ExperimentalMaterial3Api::class)

package com.glambiase.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glambiase.core.presentation.designsystem.AnalyticsIcon
import com.glambiase.core.presentation.designsystem.LogoIcon
import com.glambiase.core.presentation.designsystem.LogoutIcon
import com.glambiase.core.presentation.designsystem.RunIcon
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambiase.core.presentation.designsystem.components.RunTrackerScaffold
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerFloatingActionButton
import com.glambiase.core.presentation.designsystem.components.topappbars.DropDownItem
import com.glambiase.core.presentation.designsystem.components.topappbars.RunTrackerTopAppBar
import com.glambiase.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        onAction = { action ->
            when (action) {
                RunOverviewAction.OnStartRunClick -> onStartRunClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )

    RunTrackerScaffold(
        topAppBar = {
            RunTrackerTopAppBar(
                showBackButton = false,
                title = stringResource(id = R.string.run_overview_main_text),
                menuItems = listOf(
                    DropDownItem(
                        icon = AnalyticsIcon,
                        title = stringResource(R.string.analytics)
                    ),
                    DropDownItem(
                        icon = LogoutIcon,
                        title = stringResource(R.string.logout)
                    )
                ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }
                },
                scrollBehavior = scrollBehavior,
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            RunTrackerFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartRunClick)
                }
            )
        }
    ) { paddingValues ->
    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunTrackerTheme {
        RunOverviewScreen(
            onAction = {}
        )
    }
}
