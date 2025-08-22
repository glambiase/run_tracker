package com.glambiase.run.presentation.active_run

import com.glambiase.core.presentation.ui.UiText

sealed interface ActiveRunEvent {
    data object RunSaved : ActiveRunEvent
    data class Error(val error: UiText) : ActiveRunEvent
}