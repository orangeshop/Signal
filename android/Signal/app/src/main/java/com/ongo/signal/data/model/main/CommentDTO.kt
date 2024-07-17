package com.ongo.signal.data.model.main

data class CommentDTO(
    val commentId: String,
    val content: String,
    val userId: String = "",
    val userName: String = "도라에몽",
    val userProfile: String = ""
)