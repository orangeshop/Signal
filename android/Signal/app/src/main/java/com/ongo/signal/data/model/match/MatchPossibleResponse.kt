package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchPossibleResponse(
    @SerializedName("user") val user: MatchPossibleUser,
    @SerializedName("location") val location: MatchPossibleLocation,
    @SerializedName("dist") val dist: Double,
    @SerializedName("quadrant") val quadrant: Int,
    @SerializedName("url") val url: String? = null
)
