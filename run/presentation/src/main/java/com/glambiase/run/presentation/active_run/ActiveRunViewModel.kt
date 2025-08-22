package com.glambiase.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glambiase.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val hasLocationPermission = MutableStateFlow(false)

    init {
        hasLocationPermission
            .onEach { hasLocationPermission ->
                if (hasLocationPermission) runningTracker.startObservingLocation()
                else runningTracker.stopObservingLocation()
            }
            .launchIn(viewModelScope)

        // test
        runningTracker.currentLocation
            .onEach {
                Timber.d("current location: $it")
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnBackClick -> Unit // we navigate directly from the UI
            ActiveRunAction.OnStartAndStopRunClick -> TODO()
            ActiveRunAction.OnFinishRunClick -> TODO()
            ActiveRunAction.OnResumeRunClick -> TODO()
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.isLocationPermissionGranted
                state = state.copy(
                    showLocationPermissionRationale = action.showLocationPermissionRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationPermissionRationale = action.showNotificationPermissionRationale
                )
            }
            ActiveRunAction.DismissPermissionRationale -> {
                state = state.copy(
                    showLocationPermissionRationale = false,
                    showNotificationPermissionRationale = false
                )
            }
        }
    }
}