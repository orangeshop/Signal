package com.ongo.signal.network

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {

    @POST("/user/login")
    suspend fun postLoginRequest(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/token/regist")
    suspend fun postRegistToken(
        @Query("user_id") userId: Long,
        @Query("token") token: String,
    ) : Response<FCMTokenResponse>
}