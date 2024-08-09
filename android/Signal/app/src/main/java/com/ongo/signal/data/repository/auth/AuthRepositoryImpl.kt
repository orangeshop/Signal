package com.ongo.signal.data.repository.auth

import com.ongo.signal.config.UserSession
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.network.AuthApi
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun postLogin(request: LoginRequest): Result<LoginResponse?> {
        val req = authApi.postLoginRequest(request)
        Timber.d("login : $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun deleteUser(accessToken: String, refreshToken: String): Int {
        val req = authApi.postLogoutRequest(accessToken, refreshToken)
        Timber.d("로그아웃 ${req} 요청은 ${req}")
        return if (req.isSuccessful) {
            1
        } else {
            0
        }
    }

    override suspend fun renewalToken(refreshToken: String): Result<LoginResponse?> {
        val req = authApi.renewalRefreshToken(refreshToken)
        Timber.d("리뉴얼 리프레시 ${req}")
        return if (req.isSuccessful) {
            req.body()?.let { loginResponse ->
                UserSession.accessToken = loginResponse.accessToken
                UserSession.refreshToken = loginResponse.refreshToken
            }
            return Result.success(req.body())
        } else {
            return Result.failure(Exception())
        }
    }
}