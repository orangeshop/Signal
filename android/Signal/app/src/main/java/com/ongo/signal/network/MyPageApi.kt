package com.ongo.signal.network

import com.ongo.signal.data.model.main.BoardDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MyPageApi {

    @GET("user/board/{userId}")
    suspend fun getMySignal(@Path("userId") userId: Long): Response<List<BoardDTO>>

    @GET("user/comment/{userId}")
    suspend fun getMyCommentSignal(@Path("userId") userId: Long): Response<List<BoardDTO>>
}