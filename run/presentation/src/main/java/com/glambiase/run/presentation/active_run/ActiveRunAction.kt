package com.glambiase.run.presentation.active_run

sealed interface ActiveRunAction {
    data object OnBackClick : ActiveRunAction
    data object OnStartAndStopRunClick : ActiveRunAction
    data object OnResumeRunClick : ActiveRunAction
    data object OnFinishRunClick : ActiveRunAction
    data class SubmitLocationPermissionInfo(val isLocationPermissionGranted: Boolean, val showLocationPermissionRationale: Boolean) : ActiveRunAction
    data class SubmitNotificationPermissionInfo(val isNotificationPermissionGranted: Boolean, val showNotificationPermissionRationale: Boolean) : ActiveRunAction
    data object DismissPermissionRationale : ActiveRunAction
    class OnRunProcessed(val mapPictureBytes: ByteArray): ActiveRunAction
}