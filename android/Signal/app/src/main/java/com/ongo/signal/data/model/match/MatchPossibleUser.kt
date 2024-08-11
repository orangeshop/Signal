package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchPossibleUser(
    @SerializedName("userId") val userId: Long,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("comment") val comment: String = "안녕하세요",
    @SerializedName("score") val score: Int = 0
)
