package com.ongo.signal.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

object SpannableStringUtils {
    fun getSpannableString(fullText: String, keyword: String, color: Int): SpannableString {
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf(keyword)
        if (start == -1) return spannableString
        val end = start + keyword.length
        spannableString.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
}
