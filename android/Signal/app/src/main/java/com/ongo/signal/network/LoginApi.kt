package com.ongo.signal.network

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.IDCheckResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.data.model.login.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApi {

    @POST("/user/login")
    suspend fun postLoginRequest(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/token/regist")
    suspend fun postRegistToken(
        @Query("userId") userId: Long,
        @Query("token") token: String,
    ): Response<FCMTokenResponse>

    @POST("/user/logout")
    suspend fun postLogoutRequest(
        @Header("RefreshToken") token: String,
    ): Response<Void>

    @POST("/user/create")
    suspend fun postSignUpRequest(
        @Body request: SignupRequest
    ): Response<LoginResponse>

    @POST("/user/duplicate/{id}")
    suspend fun postCheckPossibleId(
        @Path("id") loginId: String,
    ): Response<IDCheckResponse>
}