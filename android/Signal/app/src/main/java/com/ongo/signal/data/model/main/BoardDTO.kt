package com.ongo.signal.data.model.main

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BoardDTO(
    val comments: ArrayList<CommentDTOItem>,
    val content: String,
    val createdDate: String? = null,
    val id: Int = 0,
    val liked: Int = 0,
    val modifiedDate: String? = null,
    val reference: Int = 0,
    val title: String,
    val type: Int? = 0,
    val userId: Int,
    val writer: String
) {
    fun getFormattedCreatedDate(): String? {
        return createdDate?.let { formatDate(it) }
    }

    fun getFormattedModifiedDate(): String? {
        return modifiedDate?.let { formatDate(it) }
    }

    private fun formatDate(dateTime: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDateTime.parse(dateTime)
        return parsedDate.format(formatter)
    }

    fun getCommentCount(): Int {
        return comments.size
    }
}