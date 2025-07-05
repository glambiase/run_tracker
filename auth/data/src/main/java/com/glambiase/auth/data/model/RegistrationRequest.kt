package com.glambiase.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String
)