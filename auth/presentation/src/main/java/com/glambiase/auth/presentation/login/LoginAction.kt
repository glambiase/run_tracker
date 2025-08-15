package com.glambiase.auth.presentation.login

sealed interface LoginAction {
    data object OnLoginClick : LoginAction
    data object OnPasswordVisibilityClick : LoginAction
    data object OnRegisterClick : LoginAction
}