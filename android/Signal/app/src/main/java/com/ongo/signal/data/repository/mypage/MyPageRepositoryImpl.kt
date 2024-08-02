package com.ongo.signal.data.repository.mypage

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.network.MyPageApi
import retrofit2.Response
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
}