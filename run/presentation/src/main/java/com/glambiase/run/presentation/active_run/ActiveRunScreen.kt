@file:OptIn(ExperimentalMaterial3Api::class)

package com.glambiase.run.presentation.active_run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambiase.core.presentation.designsystem.StartIcon
import com.glambiase.core.presentation.designsystem.StopIcon
import com.glambiase.core.presentation.designsystem.components.RunTrackerScaffold
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerFloatingActionButton
import com.glambiase.core.presentation.designsystem.components.topappbar.RunTrackerTopAppBar
import com.glambiase.run.presentation.R
import com.glambiase.run.presentation.active_run.composables.RunDataCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit
) {
    RunTrackerScaffold(
        hasGradient = false,
        topAppBar = {
            RunTrackerTopAppBar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run_main_text),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                }
            )
        },
        floatingActionButton = {
            RunTrackerFloatingActionButton(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                onClick = {
                    onAction(ActiveRunAction.OnStartAndStopRunClick)
                },
                contentDescription = stringResource(
                    id = if (state.shouldTrack) R.string.stop_run else R.string.start_run
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunTrackerTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {}
        )
    }
}