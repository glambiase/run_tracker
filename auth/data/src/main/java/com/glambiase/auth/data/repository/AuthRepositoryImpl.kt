package com.glambiase.auth.data.repository

import com.glambiase.auth.data.model.LoginRequest
import com.glambiase.auth.data.model.LoginResponse
import com.glambiase.auth.data.model.RegistrationRequest
import com.glambiase.auth.domain.repository.AuthRepository
import com.glambiase.core.data.networking.post
import com.glambiase.core.domain.AuthInfo
import com.glambiase.core.domain.SessionStorage
import com.glambiase.core.domain.util.DataError
import com.glambiase.core.domain.util.EmptyResult
import com.glambiase.core.domain.util.Result
import com.glambiase.core.domain.util.asEmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> =
        httpClient.post<RegistrationRequest, Unit>(
            route = "/register",
            body = RegistrationRequest(
                email = email,
                password = password
            )
        )

    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        )
        if (result is Result.Success) {
            sessionStorage.set(
                authInfo = AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        }
        return result.asEmptyResult()
    }
}