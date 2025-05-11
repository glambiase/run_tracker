package com.glambiase.auth.presentation.registration

sealed interface RegistrationAction {
    data object OnLoginClick : RegistrationAction
    data object OnPasswordVisibilityClick : RegistrationAction
    data object OnRegisterClick : RegistrationAction
}