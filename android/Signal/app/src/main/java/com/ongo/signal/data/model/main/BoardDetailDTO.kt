package com.ongo.signal.data.model.main

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BoardDetailDTO(
    val id: Long,
    val writer: String,
    val userId: Long,
    val member: MemberDTO,
    val title: String,
    val content: String,
    val reference: Long,
    val liked: Long,
    val type: Long,
    val createdDate: String,
    val modifiedDate: String,
    val comments: List<CommentDTOItem>,
    val tags: List<TagDTO>,
    val fileUrls: List<String>
) {
    fun getFormattedCreatedDate(): String {
        return formatDate(createdDate)
    }

    fun getFormattedModifiedDate(): String {
        return formatDate(modifiedDate)
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
