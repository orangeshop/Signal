package com.ongo.signal.data.model.login

import com.google.gson.annotations.SerializedName

data class FCMTokenResponse(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("token") val token: String,
)
