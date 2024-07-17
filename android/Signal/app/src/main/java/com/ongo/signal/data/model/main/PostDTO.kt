package com.ongo.signal.data.model.main

import java.util.Date

data class PostDTO(
    val postId: String,
    val title: String,
    val content: String,
    val profile: String,
    val name: String,
    val date: Date,
    val image: String? = null,
    val tags: List<TagDTO>,
    val likeCount: Int,
    val commentCount: Int,
    val comment: List<String>
)