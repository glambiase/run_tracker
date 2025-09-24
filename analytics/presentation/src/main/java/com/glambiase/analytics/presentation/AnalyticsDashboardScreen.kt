@file:OptIn(ExperimentalMaterial3Api::class)

package com.glambiase.analytics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glambiase.analytics.presentation.composables.AnalyticsCard
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambiase.core.presentation.designsystem.components.RunTrackerScaffold
import com.glambiase.core.presentation.designsystem.components.topappbars.RunTrackerTopAppBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsDashboardScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AnalyticsDashboardViewModel = koinViewModel()
) {
    AnalyticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                AnalyticsAction.OnBackClick -> onBackClick()
            }
        }
    )
}

@Composable
fun AnalyticsDashboardScreen(
    state: AnalyticsDashboardState?,
    onAction: (AnalyticsAction) -> Unit
) {
    RunTrackerScaffold(
        topAppBar = {
            RunTrackerTopAppBar(
                showBackButton = true,
                title = stringResource(id = R.string.analytics_title),
                onBackClick = {
                    onAction(AnalyticsAction.OnBackClick)
                }
            )
        },
    ) { paddingValues ->
        if (state == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AnalyticsCard(
                        text = stringResource(id = R.string.total_distance_run),
                        value = state.totalDistanceRun,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AnalyticsCard(
                        text = stringResource(id = R.string.total_time_run),
                        value = state.totalTimeRun,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AnalyticsCard(
                        text = stringResource(id = R.string.avg_distance_per_run),
                        value = state.avgDistance,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AnalyticsCard(
                        text = stringResource(id = R.string.avg_pace_per_run),
                        value = state.avgPace,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AnalyticsCard(
                        text = stringResource(id = R.string.fastest_ever_run),
                        value = state.fastestEverRun,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AnalyticsDashboardScreenPreview() {
    RunTrackerTheme {
        AnalyticsDashboardScreen(
            state = AnalyticsDashboardState(
                totalDistanceRun = "7.2 km",
                totalTimeRun = "0d 1h 2m",
                fastestEverRun = "16 km/h",
                avgDistance = "3.6 km",
                avgPace = "05:50"
            ),
            onAction = {}
        )
    }
}