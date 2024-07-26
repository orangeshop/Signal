package com.ongo.signal.data.model.main

data class CommentDTOItem(
    val boardId: Int,
    val content: String,
    val createdDate: String?,
    val id: Int,
    val modifiedDate: String?,
    val userId: Int,
    val url: String,
    val writer: String
)
