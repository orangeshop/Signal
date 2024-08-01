package com.ongo.signal.data.model.main

data class ReviewDTO(
    val reviewId: String,
    val userName: String,
    val review: String,
    val rating: Float,
    val profile: String
)
