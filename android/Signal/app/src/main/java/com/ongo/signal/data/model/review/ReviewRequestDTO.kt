package com.ongo.signal.data.model.review

import com.google.gson.annotations.SerializedName

data class ReviewRequestDTO(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("content") val content: String,
    @SerializedName("writer_id") val writerId: Long,
    @SerializedName("star") val star: Int
)