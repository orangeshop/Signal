package com.ongo.signal.data.repository.mypage

import com.ongo.signal.data.model.login.LoginUserResponse
import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.my.MyProfileResponse
import com.ongo.signal.data.model.my.ProfileEditRequest
import com.ongo.signal.network.MyPageApi
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPageRepositoryImpl @Inject constructor(private val myPageApi: MyPageApi) :
    MyPageRepository {
    override suspend fun getMySignal(userId: Long): Response<List<BoardDTO>> {
        return myPageApi.getMySignal(userId)
    }

    override suspend fun getMyCommentSignal(userId: Long): Response<List<BoardDTO>> {
        return myPageApi.getMyCommentSignal(userId)
    }

    override suspend fun getMyProfile(): Result<MyProfileResponse?> {
        val req = myPageApi.getMyProfile()
        Timber.d("프로필 받아옴 $req")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }

    override suspend fun putUserProfile(
        userId: Long,
        request: ProfileEditRequest
    ): Result<LoginUserResponse?> {
        val req = myPageApi.putUserProfile(userId, request)
        Timber.d("프로필 수정 확인 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }


}