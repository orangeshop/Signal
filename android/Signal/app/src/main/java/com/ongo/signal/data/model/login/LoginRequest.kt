package com.ongo.signal.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("loginId") val loginId: String,
    @SerializedName("password") val password: String,
)
