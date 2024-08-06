package com.ongo.signal.data.model.my

import com.google.gson.annotations.SerializedName

data class MyProfileResponse(
    @SerializedName("httpStatus") val httpStatus: String,
    @SerializedName("code") val code: Int,
    @SerializedName("data") val myProfileData: MyProfileData,
)
