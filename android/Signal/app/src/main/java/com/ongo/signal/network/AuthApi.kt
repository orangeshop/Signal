package com.ongo.signal.network

import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/user/login")
    suspend fun postLoginRequest(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/user/logout")
    suspend fun postLogoutRequest(
        @Header("Authorization") accessToken: String,
        @Header("RefreshToken") refreshToken: String,
    ): Response<Void>

    @POST("user/refresh")
    suspend fun renewalRefreshToken(
        @Header("RefreshToken") refreshToken: String,
    ) : Response<LoginResponse>

    @POST("/oauth/naver")
    suspend fun naverLogin(
        @Query("token") token: String
    ): Response<LoginResponse>

}