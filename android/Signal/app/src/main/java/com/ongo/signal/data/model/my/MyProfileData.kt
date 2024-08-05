package com.ongo.signal.data.model.my

import com.google.gson.annotations.SerializedName

data class MyProfileData(
    @SerializedName("userId") val userId: Long,
    @SerializedName("loginId") val loginId: String,
    @SerializedName("password") val password: String,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("profileImage") val profileImage: String,
    @SerializedName("comment") val comment: String,
)
