package com.ongo.signal.data.model.main

data class BoardRequestDTO(
    val userId: Long,
    val writer: String,
    val title: String,
    val content: String,
    val type: Long,
    val tags: List<TagDTO>
)