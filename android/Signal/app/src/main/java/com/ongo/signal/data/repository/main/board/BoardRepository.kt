package com.ongo.signal.data.repository.main.board

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardImagesDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.data.model.main.UpdateBoardDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

interface BoardRepository {

    suspend fun writeBoard(boardRequestDTO: BoardRequestDTO): Response<BoardDTO>
    suspend fun readBoard(pageNum: Int, limit: Int): Response<List<BoardDTO>>
    suspend fun readBoardById(boardId: Long): Response<BoardDTO>
    suspend fun updateBoard(boardId: Long, updateBoardDTO: UpdateBoardDTO): Response<BoardDTO>
    suspend fun deleteBoard(boardId: Long): Response<ResponseBody>

    //    suspend fun uploadImage(boardId: Long, image: MultipartBody.Part): Response<ResponseBody>
//    suspend fun boardLike(boardId: Long): Response<Long>

    //    suspend fun getBoardImages(): Response<BoardImagesDTO>
    suspend fun searchBoard(keyword: String): Response<List<BoardDTO>>
    suspend fun getHotSignal(): Response<List<BoardDTO>>
    suspend fun getRecentSignalByTag(tag: String, page: Int, limit: Int): Response<List<BoardDTO>>
    suspend fun getHotSignalByTag(tag: String, page: Int, limit: Int): Response<List<BoardDTO>>
    suspend fun boardLike(boardId: Long, userId: Long): Response<Long>

}