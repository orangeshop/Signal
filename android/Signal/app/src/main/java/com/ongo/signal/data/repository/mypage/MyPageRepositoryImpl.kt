package com.ongo.signal.data.repository.mypage

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.my.MyProfileResponse
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

    override suspend fun getMyProfile(token: String): Result<MyProfileResponse?> {
        val req = myPageApi.getMyProfile(token)
        Timber.d("마이 프로필 조회 확인 ${req}")
        return if (req.isSuccessful) {
            Result.success(req.body())
        } else {
            Result.failure(Exception())
        }
    }
}