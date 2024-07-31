package com.ongo.signal.data.model.main

import android.net.Uri

sealed class ImageItem {
    data class UriItem(val uri: Uri) : ImageItem()
    data class UrlItem(val url: String) : ImageItem()
}