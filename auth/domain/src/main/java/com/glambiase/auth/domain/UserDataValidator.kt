package com.glambiase.auth.domain

class UserDataValidator(
    private val patternValidator: PatternValidator
) {
    fun isValidEmail(email: String) = patternValidator.matches(email.trim())

    fun validatePassword(password: String) =
        PasswordValidationState(
            hasMinLength = password.length >= MIN_PWD_LENGTH,
            hasNumber = password.any { it.isDigit() },
            hasLowerCaseChar = password.any { it.isLowerCase() },
            hasUpperCaseChar = password.any { it.isUpperCase() }
        )

    companion object {
        const val MIN_PWD_LENGTH = 9
    }
}