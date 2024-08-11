package com.ongo.signal.util

import com.ongo.signal.R

fun tierSetting(count: Int): Int {
    return when (count) {
        in 1..5 -> R.drawable.silver
        in 6..10 -> R.drawable.gold
        in 11..15 -> R.drawable.platinum
        in 16..Int.MAX_VALUE -> R.drawable.king
        else -> R.drawable.bronze
    }
}