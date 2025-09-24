package com.glambiase.run.presentation.run_overview

import com.glambiase.run.presentation.run_overview.model.RunUI

sealed interface RunOverviewAction {
    data object OnStartRunClick : RunOverviewAction
    data object OnAnalyticsClick : RunOverviewAction
    data object OnLogoutClick : RunOverviewAction
    data class DeleteRun(val runUI: RunUI) : RunOverviewAction
}