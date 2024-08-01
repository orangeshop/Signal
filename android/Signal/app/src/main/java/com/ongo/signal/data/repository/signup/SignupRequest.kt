package com.ongo.signal.data.repository.signup

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("password") val password: String,
    @SerializedName("login_id") val loginId: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
)
