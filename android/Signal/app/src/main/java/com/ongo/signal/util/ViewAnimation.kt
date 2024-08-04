package com.ongo.signal.util

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.ongo.signal.R

object ViewAnimation {
    fun fadeOut(view: View, context: Context) {
        val fadeOut = AnimationUtils.loadAnimation(context, R.anim.anim_fade_out)
        view.startAnimation(fadeOut)
        view.visibility = View.GONE
    }

    fun fadeIn(view: View, context: Context) {
        view.visibility = View.VISIBLE
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_from_right_fade_in)
        view.startAnimation(fadeIn)
    }
}