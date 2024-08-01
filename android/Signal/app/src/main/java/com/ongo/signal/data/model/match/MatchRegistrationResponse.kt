package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchRegistrationResponse(
    val latitude: Double,
    val location_id: Long,
    val longitude: Double,
    @SerializedName("user_id") val userId: Long
)