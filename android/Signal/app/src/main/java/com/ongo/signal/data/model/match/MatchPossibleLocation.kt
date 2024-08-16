package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchPossibleLocation(
    @SerializedName("location_id") val locationId: Long,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("user_id") val userId: Long,
)
