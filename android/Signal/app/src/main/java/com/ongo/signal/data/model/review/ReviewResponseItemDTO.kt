package com.ongo.signal.data.model.review

import com.google.gson.annotations.SerializedName

data class ReviewResponseItemDTO(
    @SerializedName("review_id") val reviewId: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("score") val score: Long,
    @SerializedName("type") val type: String,
    @SerializedName("content") val content: String,
    @SerializedName("name") val name: String,
    @SerializedName("writer_id") val writerId: Long,
    @SerializedName("star") val star: Int,
    @SerializedName("url") val url: String
)