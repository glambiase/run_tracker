package com.glambiase.auth.domain.repository

import com.glambiase.core.domain.util.DataError
import com.glambiase.core.domain.util.EmptyResult

interface AuthRepository {

    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}