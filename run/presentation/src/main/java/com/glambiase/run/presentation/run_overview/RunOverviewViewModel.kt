package com.glambiase.run.presentation.run_overview

import androidx.lifecycle.ViewModel

class RunOverviewViewModel(
) : ViewModel() {

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnStartRunClick -> Unit // We navigate directly from the UI
            RunOverviewAction.OnAnalyticsClick -> TODO()
            RunOverviewAction.OnLogoutClick -> TODO()
        }
    }
}