package com.ongo.signal.data.repository.main.comment

import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.data.model.main.CommentRequestDTO
import okhttp3.ResponseBody
import retrofit2.Response

interface CommentRepository {

    suspend fun readComments(boardId: Int): Response<CommentDTO>
    suspend fun writeComment(boardId: Long, commentDto: CommentDTOItem): Response<CommentDTOItem>
    suspend fun updateComment(
        boardId: Long,
        id: Long,
        commentDto: CommentRequestDTO
    ): Response<CommentDTOItem>

    suspend fun deleteComment(boardId: Long, id: Long): Response<ResponseBody>

}
