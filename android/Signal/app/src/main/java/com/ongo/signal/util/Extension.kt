package com.ongo.signal.util

import com.ongo.signal.R

fun tierSetting(count: Int): Int {

    if( 1 <= count && count <= 5){
        return R.drawable.silver
    }else if( 6 <= count && count <= 10){
        return R.drawable.gold
    }else if( 11 <= count && count <= 15){
        return R.drawable.platinum
    }else if( 16 <= count){
        return R.drawable.king
    }

    return R.drawable.bronze
}