package com.ongo.signal.data.model.match

import android.graphics.Bitmap

data class Dot(
    val userId: Long,
    val userName: String,
    val profileImage: String = "https://github.com/user-attachments/assets/8352be5b-d960-4925-bb5f-78a74dfbab05",
    val comment: String = "안녕하세요",
    val distance: Double,
    val quadrant: Int,
    var x: Float = 0f, var y: Float = 0f,
    var alpha: Float = 0f,
    var isFocused: Boolean = false,
    var profileBitmap: Bitmap? = null,
)