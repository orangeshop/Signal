package com.ongo.signal.data.repository.main.comment

import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import retrofit2.Response

interface CommentRepository {

    suspend fun readComments(boardId: Int): Response<CommentDTO>
    suspend fun writeComment(boardId: Long, commentDto: CommentDTOItem): Response<CommentDTOItem>

}
