package com.ongo.signal.data.model.main

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BoardDTO(
    val comments: ArrayList<CommentDTOItem>? = arrayListOf(),
    val content: String,
    val createdDate: String? = null,
    val id: Long = 0,
    val liked: Long = 0,
    val modifiedDate: String? = null,
    val reference: Long = 0,
    val title: String,
    val type: Long? = 0,
    val userId: Long,
    val writer: String,
    @SerializedName("fileUrls") val imageUrls: List<String>? = listOf(),
    val tags: List<TagDTO>? = emptyList()
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
        return comments?.size ?: 0
    }
}