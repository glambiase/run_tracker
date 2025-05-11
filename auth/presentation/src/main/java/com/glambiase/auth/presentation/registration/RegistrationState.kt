package com.glambiase.auth.presentation.registration

import androidx.compose.foundation.text.input.TextFieldState
import com.glambiase.auth.domain.PasswordValidationState

data class RegistrationState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isRegistering: Boolean = false,
    val canRegister: Boolean = false
)