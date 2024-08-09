package com.ongo.signal.ui.main.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.BoardImagesItemDTO
import com.ongo.signal.data.repository.main.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val _boardImages = MutableStateFlow<Map<Long, List<BoardImagesItemDTO>>>(emptyMap())
    val boardImages: StateFlow<Map<Long, List<BoardImagesItemDTO>>> = _boardImages

    fun updateImage(
        boardId: Long,
        uris: List<Uri>,
        existingUrls: List<String>,
        context: Context
    ): Deferred<Result<List<String?>>> = viewModelScope.async {
        Timber.d("uploadImages called with boardId: $boardId, uris: $uris")
        runCatching {
            val newParts = uris.map { uri ->
                val file = createFileFromUri(uri, context)
                Timber.tag("imageViewModel")
                    .d("File created: ${file.absolutePath}, size: ${file.length()}")
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            }

            val existingParts = existingUrls.map { url ->
                withContext(Dispatchers.IO) {
                    val file = downloadFileFromUrl(url, context)
                    Timber.tag("imageViewModel")
                        .d("Downloaded file from URL: ${file.absolutePath}, size: ${file.length()}")
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                }
            }

            val parts = newParts + existingParts
            val response = imageRepository.updateImage(boardId, parts)
            Timber.d("Server response: ${response.raw()}")
            if (response.isSuccessful) {
                val responseBodyString = response.body()?.toString()
                Timber.d("Response body as string: $responseBodyString")

                responseBodyString?.let {
                    it.split(",").map { url -> url.trim().replace("[", "").replace("]", "") }
                } ?: emptyList<String?>()
            } else {
                val error = response.errorBody()?.string()
                Timber.e("Failed to upload images: $error")
                emptyList<String?>()
            }
        }.onFailure { e ->
            Timber.e(e, "Failed to upload images")
        }
    }

    private fun createFileFromUri(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            Timber.d("File created with size: ${file.length()} bytes")
        } catch (e: IOException) {
            Timber.e(e, "Failed to create file from URI")
        }
        return file
    }


    private fun downloadFileFromUrl(url: String, context: Context): File {
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        try {
            val urlConnection = URL(url).openConnection()
            val inputStream = urlConnection.getInputStream()
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            Timber.d("Downloaded file from URL with size: ${file.length()} bytes")
        } catch (e: IOException) {
            Timber.e(e, "Failed to download file from URL")
        }
        return file
    }
}
