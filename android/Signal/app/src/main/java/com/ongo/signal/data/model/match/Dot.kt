package com.ongo.signal.data.model.match

import android.graphics.Bitmap

data class Dot(
    val userId: Long,
    val userName: String,
    val profileImage: String = "https://github.com/user-attachments/assets/f463e225-6d1d-455f-9267-0d4e8602f565",
    val comment: String = "안녕하세요",
    val distance: Double,
    val quadrant: Int,
    var x: Float = 0f, var y: Float = 0f,
    var alpha: Float = 0f,
    var isFocused: Boolean = false,
    var profileBitmap: Bitmap? = null,
)