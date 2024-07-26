package com.ongo.signal.data.repository.main.board

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import okhttp3.ResponseBody
import retrofit2.Response

interface BoardRepository {

    suspend fun writeBoard(boardRequestDTO: BoardRequestDTO): Response<BoardDTO>
    suspend fun readBoard(): Response<List<BoardDTO>>
    suspend fun readBoardById(boardId: Int): Response<BoardDTO>
    suspend fun updateBoard(boardId: Int, boardRequestDTO: BoardRequestDTO): Response<BoardRequestDTO>
    suspend fun deleteBoard(boardId: Int): Response<ResponseBody>

}