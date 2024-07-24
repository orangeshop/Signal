package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchRegistrationRequest(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("user_id") val userId: Long
)