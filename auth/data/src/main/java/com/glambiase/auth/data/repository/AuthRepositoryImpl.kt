package com.glambiase.auth.data.repository

import com.glambiase.auth.data.model.RegistrationRequest
import com.glambiase.auth.domain.repository.AuthRepository
import com.glambiase.core.data.networking.post
import com.glambiase.core.domain.util.DataError
import com.glambiase.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> =
        httpClient.post<RegistrationRequest, Unit>(
            route = "/register",
            body = RegistrationRequest(
                email = email,
                password = password
            )
        )
}