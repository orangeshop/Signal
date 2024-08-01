package com.ongo.signal.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ongo.signal.R
import timber.log.Timber

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("imageUri")
    fun loadImage(view: ImageView, uri: Uri?) {
        Glide.with(view.context)
            .load(uri)
            .into(view)
    }
}