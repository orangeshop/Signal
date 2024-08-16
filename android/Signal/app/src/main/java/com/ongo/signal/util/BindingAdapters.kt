package com.ongo.signal.util

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.ongo.signal.R

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

    @JvmStatic
    @BindingAdapter("profile")
    fun loadProfile(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.basic_profile)
            .circleCrop()
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("imageUrls")
    fun loadImages(view: ImageView, urls: List<String>?) {
        val url = urls?.firstOrNull()
        Glide.with(view.context)
            .load(url)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("listSize")
    fun setListSize(view: TextView, list: List<*>?) {
        val size = list?.size ?: 0
        view.text = " ($size)"
    }

    @JvmStatic
    @BindingAdapter("app:isLiked")
    fun setThumbIcon(imageView: ImageView, isLiked: Boolean) {
        imageView.setImageResource(
            if (isLiked) R.drawable.baseline_thumb_up_alt_24_purple
            else R.drawable.baseline_thumb_up_off_alt_24
        )
    }
}