package com.glambiase.auth.presentation.login

import com.glambiase.core.presentation.ui.UiText

sealed interface LoginEvent {
    data object Success : LoginEvent
    data class Error(val error: UiText) : LoginEvent
}