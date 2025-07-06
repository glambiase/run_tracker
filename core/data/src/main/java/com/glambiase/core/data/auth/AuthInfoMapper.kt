package com.glambiase.core.data.auth

import com.glambiase.core.domain.AuthInfo


fun AuthInfoSerializable.toAuthInfo() =
    AuthInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId
    )

fun AuthInfo.toAuthInfoSerializable() =
    AuthInfoSerializable(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = userId
    )