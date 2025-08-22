package com.glambiase.run.presentation.active_run

sealed interface ActiveRunAction {
    data object OnStartAndStopRunClick : ActiveRunAction
    data object OnFinishRunClick : ActiveRunAction
    data object OnResumeRunClick : ActiveRunAction
    data object OnBackClick : ActiveRunAction
}