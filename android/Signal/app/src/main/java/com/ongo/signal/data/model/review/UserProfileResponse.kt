package com.ongo.signal.data.model.review

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("profileImage") val profileImage: String,
    @SerializedName("name") val name: String,
)
