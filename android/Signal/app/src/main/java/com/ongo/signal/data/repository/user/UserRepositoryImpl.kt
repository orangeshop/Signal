package com.ongo.signal.data.repository.user

import com.ongo.signal.data.model.login.FCMTokenResponse
import com.ongo.signal.data.model.login.IDCheckResponse
import com.ongo.signal.data.model.login.LoginRequest
import com.ongo.signal.data.model.login.LoginResponse
import com.ongo.signal.data.model.login.ProfileImageResponse
import com.ongo.signal.data.model.login.SignupRequest
import com.ongo.signal.network.UserApi
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
//    override suspend fun postLogin(request: LoginRequest): Result<LoginResponse?> {
//        val req = userApi.postLoginRequest(request)
//        Timber.d("login : $req")
//        return if (req.isSuccessful) {
//            Result.success(req.body())
//        } else {
//            Result.failure(Exception())
//        }
//    }

    override suspend fun postFCMToken(userId: Long, token: String): Result<FCMTokenResponse?> {
        val req = userApi.postRegistToken(userId = userId, token = token)
        Timber.d("토큰 서버에 등록 : $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

//    override suspend fun deleteUser(accessToken: String, refreshToken: String): Int {
//        val req = userApi.postLogoutRequest(accessToken, refreshToken)
//        Timber.d("로그아웃 ${req} 요청은 ${req}")
//        return if (req.isSuccessful) {
//            1
//        } else {
//            0
//        }
//    }

    override suspend fun postSignup(request: SignupRequest): Result<LoginResponse?> {
        val req = userApi.postSignUpRequest(request)
        Timber.d("회원가입 확인 ${request} \n ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postCheckPossibleId(loginId: String): Result<IDCheckResponse?> {
        val req = userApi.postCheckPossibleId(loginId)
        Timber.d("중복아디 확인 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun postProfileImage(
        userId: Long,
        imageFile: MultipartBody.Part
    ): Result<ProfileImageResponse?> {
        val req = userApi.postProfileImage(userId, imageFile)
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun putProfileImage(
        userId: Long,
        imageFile: MultipartBody.Part
    ): Result<ProfileImageResponse?> {
        val req = userApi.putProfileImage(userId, imageFile)
        Timber.d("프로필 이미지 수정 답변 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }


}