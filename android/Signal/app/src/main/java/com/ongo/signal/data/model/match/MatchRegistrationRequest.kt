package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchRegistrationRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("memberType") val memberType: String,
    @SerializedName("user_id") val userId: Long
)