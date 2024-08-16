package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchProposeRequest(
    @SerializedName("from_id") val fromId: Long,
    @SerializedName("to_id") val toId: Long,
)
