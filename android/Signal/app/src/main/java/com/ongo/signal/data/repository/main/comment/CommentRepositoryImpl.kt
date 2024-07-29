package com.ongo.signal.data.repository.main.comment

import com.ongo.signal.data.model.main.CommentDTO
import com.ongo.signal.data.model.main.CommentDTOItem
import com.ongo.signal.network.MainApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(private val mainApi: MainApi) : CommentRepository {
    override suspend fun readComments(boardId: Int): Response<CommentDTO> {
        return mainApi.readComments(boardId)
    }

    override suspend fun writeComment(boardId: Long, commentDto: CommentDTOItem): Response<CommentDTOItem> {
        return mainApi.writeComment(boardId, commentDto)
    }


}