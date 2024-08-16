package com.ongo.signal.data.model.my

import com.google.gson.annotations.SerializedName

data class ProfileEditRequest(
    @SerializedName("loginId") val loginId: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("comment") val comment: String,
)
