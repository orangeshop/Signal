package com.ongo.signal.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@BindingAdapter("dateText")
fun bindDateText(textView: TextView, date: Date?) {
    date?.let {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        textView.text = formatter.format(date)
    }
}