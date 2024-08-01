package com.ongo.signal.data.repository.login

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.IDCheckResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.data.model.login.SignupRequest
import com.ongo.signal.network.LoginApi
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi
) : LoginRepository {
    override suspend fun postLogin(request: LoginRequest): Result<LoginResponse?> {
        val req = loginApi.postLoginRequest(request)
        Timber.d("login : $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postFCMToken(userId: Long, token: String): Result<FCMTokenResponse?> {
        val req = loginApi.postRegistToken(userId = userId, token = token)
        Timber.d("토큰 서버에 등록 : $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun deleteUser(token: String): Int {
        val req = loginApi.postLogoutRequest(token = token)
        Timber.d("로그아웃 ${req} 요청은 ${token}")
        return if (req.isSuccessful) {
            1
        } else {
            0
        }
    }

    override suspend fun postSignup(request: SignupRequest): Result<LoginResponse?> {
        val req = loginApi.postSignUpRequest(request)
        Timber.d("회원가입 확인 ${request} \n ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postCheckPossibleId(loginId: String): Result<IDCheckResponse?> {
        val req = loginApi.postCheckPossibleId(loginId)
        Timber.d("중복아디 확인 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }


}