package com.glambiase.auth.presentation.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.glambiase.auth.domain.UserDataValidator
import com.glambiase.core.presentation.ui.textAsFlow
import kotlinx.coroutines.flow.onEach

class RegistrationViewModel(
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    var state by mutableStateOf(RegistrationState())
        private set

    init {
        state.email.textAsFlow()
            .onEach { email ->
                state = state.copy(
                    isEmailValid = userDataValidator.isValidEmail(email.toString())
                )
            }

        state.password.textAsFlow()
            .onEach { password ->
                state = state.copy(
                    passwordValidationState = userDataValidator.validatePassword(password.toString())
                )
            }
    }

    fun onAction(action: RegistrationAction) {
        when (action) {
            RegistrationAction.OnLoginClick -> TODO()
            RegistrationAction.OnPasswordVisibilityClick -> TODO()
            RegistrationAction.OnRegisterClick -> TODO()
        }
    }
}