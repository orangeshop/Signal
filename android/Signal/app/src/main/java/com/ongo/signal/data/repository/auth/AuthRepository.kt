package com.ongo.signal.data.repository.auth

import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse

interface AuthRepository {
    suspend fun postLogin(request: LoginRequest): Result<LoginResponse?>

    suspend fun deleteUser(accessToken: String, refreshToken: String): Int

    suspend fun renewalToken(refreshToken: String): Result<LoginResponse?>

    suspend fun naverLogin(
        token: String
    ): Result<LoginResponse?>

    suspend fun kakaoLogin(
        token: String
    ): Result<LoginResponse?>
}