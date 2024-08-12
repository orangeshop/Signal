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
        Timber.d("로그인 데이터 : $req \n ${req.body()}")
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

    override suspend fun naverLogin(token: String): Result<LoginResponse?> {
        Timber.d("네이버 로그인 요청 토큰: $token")

        return try {
            val req = authApi.naverLogin(token)
            if (req.isSuccessful) {
                Timber.d("네이버 로그인 성공: ${req.body()}")
                Result.success(req.body())
            } else {
                Timber.e("네이버 로그인 실패: ${req.code()} - ${req.message()}")
                Result.failure(Exception("네이버 로그인 실패: ${req.code()} - ${req.message()}"))
            }
        } catch (e: Exception) {
            Timber.e("네이버 로그인 중 예외 발생: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun kakaoLogin(token: String): Result<LoginResponse?> {
        return try {
            val req = authApi.kakaoLogin(token)
            if (req.isSuccessful) {
                Result.success(req.body())
            } else {
                Result.failure(Exception("카카오 로그인 실패: ${req.errorBody()} - ${req.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}