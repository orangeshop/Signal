package com.ongo.signal.data.repository.signup

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("login_id") val loginId: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
)
