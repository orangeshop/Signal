package com.ongo.signal.util

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ongo.signal.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.baseline_person_24)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("dateText")
    fun bindDateText(textView: TextView, date: Date?) {
        date?.let {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            textView.text = formatter.format(date)
        }
    }

    @JvmStatic
    @BindingAdapter("imageUri")
    fun loadImage(view: ImageView, uri: Uri?) {
        Glide.with(view.context)
            .load(uri)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.baseline_person_24)
            .into(view)
    }
}