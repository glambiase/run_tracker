package com.glambiase.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glambiase.core.domain.SessionStorage
import com.glambiase.core.domain.run.Run
import com.glambiase.core.domain.run.RunRepository
import com.glambiase.core.domain.run.SyncRunScheduler
import com.glambiase.run.presentation.run_overview.mapper.toRunUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                syncType = SyncRunScheduler.SyncType.FetchRuns(interval = 30.minutes)
            )
        }

        runRepository.getRuns().onEach { runs ->
            val runsUI = runs.map(Run::toRunUI)
            state = state.copy(runs = runsUI)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnStartRunClick -> Unit // We navigate directly from the UI
            RunOverviewAction.OnAnalyticsClick -> Unit
            RunOverviewAction.OnLogoutClick -> logout()
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(id = action.runUI.id)
                }
            }
        }
    }

    private fun logout() =
        applicationScope.launch {
            syncRunScheduler.cancelAllSyncs()
            runRepository.deleteAllRuns()
            runRepository.logout()
            sessionStorage.set(null)
        }
}