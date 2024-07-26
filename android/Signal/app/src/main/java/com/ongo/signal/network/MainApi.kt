package com.ongo.signal.network

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MainApi {

    @POST("post")
    suspend fun writeBoard(@Body boardRequestDTO: BoardRequestDTO): Response<BoardDTO>

    @GET("board")
    suspend fun readBoard(): Response<List<BoardDTO>>

    @GET("board/{no}")
    suspend fun readBoardById(@Path("no") boardId: Int): Response<BoardDTO>

    @PUT("/board/update/{no}")
    suspend fun updateBoard(
        @Path("no") boardId: Int,
        @Body boardRequestDTO: BoardRequestDTO
    ): Response<BoardRequestDTO>

    @DELETE("/delete/{no}")
    suspend fun deleteBoard(@Path("no") boardId: Int): Response<ResponseBody>

    @GET("/comment/{boardId}")
    suspend fun readComments(@Path("boardId") boardId: Int): Response<CommentDTO>

    @POST("/comment/{boardId}")
    suspend fun writeComment(
        @Path("boardId") boardId: Long,
        @Body commentDto: CommentDTOItem
    ): Response<CommentDTOItem>
}