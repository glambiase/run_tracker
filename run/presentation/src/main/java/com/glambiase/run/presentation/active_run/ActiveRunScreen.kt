@file:OptIn(ExperimentalMaterial3Api::class)

package com.glambiase.run.presentation.active_run

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glambiase.core.presentation.designsystem.RunTrackerTheme
import com.glambiase.core.presentation.designsystem.StartIcon
import com.glambiase.core.presentation.designsystem.StopIcon
import com.glambiase.core.presentation.designsystem.components.RunTrackerScaffold
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerActionButton
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerFloatingActionButton
import com.glambiase.core.presentation.designsystem.components.buttons.RunTrackerOutlinedActionButton
import com.glambiase.core.presentation.designsystem.components.dialogs.RunTrackerDialog
import com.glambiase.core.presentation.designsystem.components.topappbars.RunTrackerTopAppBar
import com.glambiase.run.presentation.R
import com.glambiase.run.presentation.active_run.composables.RunDataCard
import com.glambiase.run.presentation.active_run.maps.RunTrackerMap
import com.glambiase.run.presentation.active_run.service.ActiveRunService
import com.glambiase.run.presentation.permissions.hasLocationPermission
import com.glambiase.run.presentation.permissions.hasNotificationPermission
import com.glambiase.run.presentation.permissions.requestRunTrackerPermissions
import com.glambiase.run.presentation.permissions.shouldShowLocationPermissionRationale
import com.glambiase.run.presentation.permissions.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        onServiceToggle = onServiceToggle
    )
}

@Composable
fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit
) {
    val context = LocalContext.current
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val hasCoarseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            val hasFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) permissions[Manifest.permission.POST_NOTIFICATIONS] == true else true

            val activity = context as ComponentActivity
            val showLocationPermissionRationale = activity.shouldShowLocationPermissionRationale()
            val showNotificationPermissionRationale = activity.shouldShowNotificationPermissionRationale()

            onAction(
                ActiveRunAction.SubmitLocationPermissionInfo(
                    isLocationPermissionGranted = hasCoarseLocationPermission && hasFineLocationPermission,
                    showLocationPermissionRationale = showLocationPermissionRationale
                )
            )

            onAction(
                ActiveRunAction.SubmitNotificationPermissionInfo(
                    isNotificationPermissionGranted = hasNotificationPermission,
                    showNotificationPermissionRationale = showNotificationPermissionRationale
                )
            )
        }
    )

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationPermissionRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationPermissionRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                isLocationPermissionGranted = context.hasLocationPermission(),
                showLocationPermissionRationale = showLocationPermissionRationale
            )
        )

        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                isNotificationPermissionGranted = context.hasNotificationPermission(),
                showNotificationPermissionRationale = showNotificationPermissionRationale
            )
        )

        if (!showLocationPermissionRationale && !showNotificationPermissionRationale) {
            permissionsLauncher.requestRunTrackerPermissions(context)
        }
    }

    LaunchedEffect(key1 = state.isRunFinished) {
        if (state.isRunFinished) onServiceToggle(false)
    }

    LaunchedEffect(key1 = state.shouldTrack) {
        if (state.shouldTrack && context.hasLocationPermission() && !ActiveRunService.isServiceActive)
            onServiceToggle(true)
    }

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
            RunTrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bitmap ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            80,
                            it
                        )
                    }
                    onAction(ActiveRunAction.OnRunProcessed(mapPictureBytes = stream.toByteArray()))
                },
                modifier = Modifier
                    .fillMaxSize()
            )
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
                    .fillMaxWidth()
            )
        }
    }

    if (!state.shouldTrack && state.hasStartedRunning) {
        RunTrackerDialog(
            title = stringResource(id = R.string.running_is_paused),
            description = stringResource(id = R.string.resume_or_finish_run),
            onDismiss = {
                onAction(ActiveRunAction.OnResumeRunClick)
            },
            primaryButton = {
                RunTrackerActionButton(
                    text = stringResource(id = R.string.resume),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.OnResumeRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            },
            secondaryButton = {
                RunTrackerOutlinedActionButton(
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSavingRun,
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        )
    }

    if (state.showLocationPermissionRationale || state.showNotificationPermissionRationale) {
        RunTrackerDialog(
            title = stringResource(id = R.string.permission_required),
            description = when {
                state.showLocationPermissionRationale && state.showNotificationPermissionRationale -> stringResource(id = R.string.location_and_notification_rationale)
                state.showLocationPermissionRationale -> stringResource(id = R.string.location_rationale)
                else /*state.showNotificationPermissionRationale*/ -> stringResource(id = R.string.notification_rationale)
            },
            onDismiss = { /* onDismiss disabled for permissions */ },
            primaryButton = {
                RunTrackerOutlinedActionButton(
                    text = stringResource(id = R.string.ok_understand),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.DismissPermissionRationale)
                        permissionsLauncher.requestRunTrackerPermissions(context)
                    }
                )
            }
        )
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunTrackerTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {},
            onServiceToggle = {}
        )
    }
}