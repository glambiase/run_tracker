package com.glambiase.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glambiase.core.domain.run.Run
import com.glambiase.core.domain.run.RunRepository
import com.glambiase.run.presentation.run_overview.mapper.toRunUI
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        runRepository.getRuns().onEach { runs ->
            val runsUI = runs.map(Run::toRunUI)
            state = state.copy(runs = runsUI)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnStartRunClick -> Unit // We navigate directly from the UI
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(id = action.runUI.id)
                }
            }
            RunOverviewAction.OnAnalyticsClick -> TODO()
            RunOverviewAction.OnLogoutClick -> TODO()
        }
    }
}