package com.ongo.signal.network

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardDetailDTO
import com.ongo.signal.data.model.main.BoardImagesDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import com.ongo.signal.data.model.main.UpdateBoardDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApi {

    @POST("post")
    suspend fun writeBoard(@Body boardRequestDTO: BoardRequestDTO): Response<BoardDTO>

    @GET("board")
    suspend fun readBoard(
        @Query("page") pageNum: Int,
        @Query("limit") limit: Int
    ): Response<List<BoardDTO>>

    @GET("board/{no}")
    suspend fun readBoardById(@Path("no") boardId: Long): Response<BoardDetailDTO>

    @PUT("board/update/{no}")
    suspend fun updateBoard(
        @Path("no") boardId: Long,
        @Body updateBoardDTO: UpdateBoardDTO
    ): Response<BoardDTO>

    @DELETE("delete/{no}")
    suspend fun deleteBoard(@Path("no") boardId: Long): Response<ResponseBody>

    @GET("comment/{boardId}")
    suspend fun readComments(@Path("boardId") boardId: Long): Response<CommentDTO>

    @POST("comment/{boardId}")
    suspend fun writeComment(
        @Path("boardId") boardId: Long,
        @Body commentDto: CommentDTOItem
    ): Response<CommentDTOItem>

    @PUT("comment/{boardId}/{id}")
    suspend fun updateComment(
        @Path("boardId") boardId: Long,
        @Path("id") id: Long,
        @Body commentDto: CommentRequestDTO
    ): Response<CommentDTOItem>

    @DELETE("comment/{boardId}/{id}")
    suspend fun deleteComment(
        @Path("boardId") boardId: Long,
        @Path("id") id: Long
    ): Response<ResponseBody>

    @Multipart
    @POST("board/{boardId}/upload")
    suspend fun uploadImage(
        @Path("boardId") boardId: Long,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("board/{boardId}/like")
    suspend fun boardLike(
        @Path("boardId") boardId: Long
    ): Response<Long>

    @GET("files")
    suspend fun getBoardImages(): Response<BoardImagesDTO>

    @GET("board/search")
    suspend fun searchBoard(@Query("keyword") keyword: String): Response<List<BoardDTO>>

    @GET("board/liked")
    suspend fun getHotSignal(): Response<List<BoardDTO>>

    @GET("tag/recent")
    suspend fun getRecentSignalByTag(
        @Query("tag") tag: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<BoardDTO>>

    @GET("tag/hot")
    suspend fun getHotSignalByTag(
        @Query("tag") tag: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<BoardDTO>>
}