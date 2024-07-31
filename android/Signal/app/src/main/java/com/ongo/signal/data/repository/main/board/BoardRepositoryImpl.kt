package com.ongo.signal.data.repository.main.board

import com.ongo.signal.data.model.main.BoardDTO
import com.ongo.signal.data.model.main.BoardRequestDTO
import com.ongo.signal.network.MainApi
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(private val mainApi: MainApi) : BoardRepository {
    override suspend fun writeBoard(boardRequestDTO: BoardRequestDTO): Response<BoardDTO> {
        return mainApi.writeBoard(boardRequestDTO)
    }

    override suspend fun readBoard(): Response<List<BoardDTO>> {
        return mainApi.readBoard()
    }

    override suspend fun readBoardById(boardId: Int): Response<BoardDTO> {
        return mainApi.readBoardById(boardId)
    }

    override suspend fun updateBoard(
        boardId: Int,
        boardRequestDTO: BoardRequestDTO
    ): Response<BoardRequestDTO> {
        return mainApi.updateBoard(boardId, boardRequestDTO)
    }

    override suspend fun deleteBoard(boardId: Int): Response<ResponseBody> {
        return mainApi.deleteBoard(boardId)
    }
}