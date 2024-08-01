package com.ongo.signal.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("accessTokenExpireTime") val accessTokenExpireTime: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("refreshTokenExpireTime") val refreshTokenExpireTime: String,
    @SerializedName("member") val userInfo: LoginUserResponse,
    @SerializedName("tokenId") val tokenId: String,
)