package com.ongo.signal.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatHomeCreate(
    @SerializedName("from_id") val fromId : Int,
    @SerializedName("to_id")val toId : Int,
)