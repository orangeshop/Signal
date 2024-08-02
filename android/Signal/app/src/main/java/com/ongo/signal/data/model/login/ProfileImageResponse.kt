package com.ongo.signal.data.model.login

import com.google.gson.annotations.SerializedName

data class ProfileImageResponse(
    @SerializedName("fileUrl") val fileUrl: String,
)
