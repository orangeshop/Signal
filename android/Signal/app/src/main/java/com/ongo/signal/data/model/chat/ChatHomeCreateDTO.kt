package com.ongo.signal.data.model.chat

import com.google.gson.annotations.SerializedName

data class ChatHomeCreateDTO(
    @SerializedName("from_id") val fromId : Int,
    @SerializedName("to_id")val toId : Int,
)