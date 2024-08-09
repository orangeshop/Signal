package com.ongo.signal.data.model.my

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyProfileData(
    @SerializedName("userId") val userId: Long = 0L,
    @SerializedName("loginId") val loginId: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("profileImage") val profileImage: String = "",
    @SerializedName("comment") val comment: String = "",
    @SerializedName("score") val score: Int = 0,

) : Parcelable
