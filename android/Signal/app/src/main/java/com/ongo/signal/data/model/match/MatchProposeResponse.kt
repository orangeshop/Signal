package com.ongo.signal.data.model.match

import com.google.gson.annotations.SerializedName

data class MatchProposeResponse(
    @SerializedName("from_id") val fromId: Long,
    @SerializedName("to_id") val toId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("comment") val comment: String,
)

//zz