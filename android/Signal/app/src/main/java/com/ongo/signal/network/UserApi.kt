package com.ongo.signal.network

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.IDCheckResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.data.model.login.ProfileImageResponse
import com.ongo.signal.data.model.login.SignupRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

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
        @Header("Authorization") accessToken: String,
        @Header("RefreshToken") refreshToken: String,
    ): Response<Void>

    @POST("/user/create")
    suspend fun postSignUpRequest(
        @Body request: SignupRequest
    ): Response<LoginResponse>

    @POST("/user/duplicate/{id}")
    suspend fun postCheckPossibleId(
        @Path("id") loginId: String,
    ): Response<IDCheckResponse>

    @Multipart
    @POST("/user/{userId}/upload")
    suspend fun postProfileImage(
        @Path("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): Response<ProfileImageResponse>

    @Multipart
    @PUT("/user/{userId}/upload")
    suspend fun putProfileImage(
        @Path("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): Response<ProfileImageResponse>

    @POST("/oauth/naver")
    suspend fun naverLogin(
        @Query("token") token: String
    ): Response<LoginResponse>
}