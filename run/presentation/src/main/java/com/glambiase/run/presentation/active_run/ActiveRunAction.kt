package com.glambiase.run.presentation.active_run

sealed interface ActiveRunAction {
    data object OnBackClick : ActiveRunAction
    data object OnStartAndStopRunClick : ActiveRunAction
    data object OnFinishRunClick : ActiveRunAction
    data object OnResumeRunClick : ActiveRunAction
    data class SubmitLocationPermissionInfo(val isLocationPermissionGranted: Boolean, val showLocationPermissionRationale: Boolean) : ActiveRunAction
    data class SubmitNotificationPermissionInfo(val isNotificationPermissionGranted: Boolean, val showNotificationPermissionRationale: Boolean) : ActiveRunAction
    data object DismissPermissionRationale : ActiveRunAction
}