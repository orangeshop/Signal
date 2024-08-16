package com.ongo.signal.data.model.main

data class CommentDTOItem(
    val boardId: Long,
    val content: String,
    val createdDate: String? = "",
    val id: Long = 0,
    val modifiedDate: String? = "",
    val userId: Long,
    val url: String = "",
    val writer: String
)
