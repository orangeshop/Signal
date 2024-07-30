package com.ongo.signal.data.repository.login

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
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

    override suspend fun deleteUser(token: String): Int{
        val req = loginApi.postLogoutRequest(token = token)
        return if(req.isSuccessful){
            1
        } else{
            0
        }
    }


}