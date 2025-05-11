package com.glambiase.auth.presentation.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegistrationViewModel : ViewModel() {

    var state by mutableStateOf(RegistrationState())
        private set

    fun onAction(action: RegistrationAction) {
        when (action) {
            RegistrationAction.OnLoginClick -> TODO()
            RegistrationAction.OnPasswordVisibilityClick -> TODO()
            RegistrationAction.OnRegisterClick -> TODO()
        }
    }
}