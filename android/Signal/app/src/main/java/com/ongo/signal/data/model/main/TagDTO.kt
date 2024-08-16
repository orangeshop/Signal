package com.ongo.signal.data.model.main

import com.google.gson.annotations.SerializedName

data class TagDTO(
    @SerializedName("id") val tagId: Int,
    @SerializedName("tagName") val tag: String
)
