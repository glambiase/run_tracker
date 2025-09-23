package com.glambiase.run.presentation.run_overview

import com.glambiase.run.presentation.run_overview.model.RunUI

data class RunOverviewState(
    val runs: List<RunUI> = emptyList()
)