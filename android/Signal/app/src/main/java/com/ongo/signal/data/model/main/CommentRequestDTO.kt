package com.ongo.signal.data.model.main

data class CommentRequestDTO(
    val boardId: Long,
    val userId: Long,
    val content: String
)