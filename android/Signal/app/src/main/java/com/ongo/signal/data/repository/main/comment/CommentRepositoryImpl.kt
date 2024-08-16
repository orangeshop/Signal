package com.ongo.signal.data.repository.main.comment

import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import com.ongo.signal.network.MainApi
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(private val mainApi: MainApi) : CommentRepository {
    override suspend fun readComments(boardId: Long): Response<CommentDTO> {
        return mainApi.readComments(boardId)
    }

    override suspend fun writeComment(
        boardId: Long,
        commentDto: CommentDTOItem
    ): Response<CommentDTOItem> {
        return mainApi.writeComment(boardId, commentDto)
    }

    override suspend fun updateComment(
        boardId: Long,
        id: Long,
        commentDto: CommentRequestDTO
    ): Response<CommentDTOItem> {
        return mainApi.updateComment(boardId, id, commentDto)
    }

    override suspend fun deleteComment(boardId: Long, id: Long): Response<ResponseBody> {
        return mainApi.deleteComment(boardId, id)
    }


}