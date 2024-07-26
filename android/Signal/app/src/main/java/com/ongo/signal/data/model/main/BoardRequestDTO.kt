package com.ongo.signal.data.model.main

data class BoardRequestDTO(
    val userId: Int,
    val writer: String,
    val title: String,
    val content: String
)