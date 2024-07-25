package com.ongo.signal.data.model.match

data class Dot(
    val userId: Long,
    val distance: Double,
    val quadrant: Int,
    var x: Float = 0f, var y: Float = 0f,
    var alpha: Float = 0f,
)