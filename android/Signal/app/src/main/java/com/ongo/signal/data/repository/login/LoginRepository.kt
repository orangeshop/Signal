package com.ongo.signal.data.repository.login

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse

interface LoginRepository {
    suspend fun postLogin(request: LoginRequest): Result<LoginResponse?>

    suspend fun postFCMToken(userId: Long, token: String): Result<FCMTokenResponse?>

    suspend fun deleteUser(token: String): Int
}