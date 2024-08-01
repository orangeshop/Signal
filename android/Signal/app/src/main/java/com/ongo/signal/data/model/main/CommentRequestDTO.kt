package com.ongo.signal.data.model.main

data class CommentRequestDTO(
    val boardId: Int,
    val userId: Int,
    val content: String
)