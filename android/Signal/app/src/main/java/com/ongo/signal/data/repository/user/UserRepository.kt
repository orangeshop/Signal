package com.ongo.signal.data.repository.user

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.IDCheckResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.data.model.login.ProfileImageResponse
import com.ongo.signal.data.model.login.SignupRequest
import okhttp3.MultipartBody

interface UserRepository {
//    suspend fun postLogin(request: LoginRequest): Result<LoginResponse?>

    suspend fun postFCMToken(userId: Long, token: String): Result<FCMTokenResponse?>

    suspend fun deleteUser(accessToken: String, refreshToken: String): Int

    suspend fun postSignup(request: SignupRequest): Result<LoginResponse?>

    suspend fun postCheckPossibleId(loginId: String): Result<IDCheckResponse?>

    suspend fun postProfileImage(
        userId: Long,
        imageFile: MultipartBody.Part
    ): Result<ProfileImageResponse?>

    suspend fun putProfileImage(
        userId: Long,
        imageFile: MultipartBody.Part
    ): Result<ProfileImageResponse?>
}