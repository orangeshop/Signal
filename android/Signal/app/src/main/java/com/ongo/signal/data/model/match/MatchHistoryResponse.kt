package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchHistoryResponse(
    @SerializedName("match_id") val matchId: Long,
    @SerializedName("proposeId") val proposeId: Long,
    @SerializedName("acceptId") val acceptId: Long,
)
