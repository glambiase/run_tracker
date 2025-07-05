package com.glambiase.auth.presentation.registration

import com.glambiase.core.presentation.ui.UiText

sealed interface RegistrationEvent {
    data object Success : RegistrationEvent
    data class Error(val error: UiText) : RegistrationEvent
}