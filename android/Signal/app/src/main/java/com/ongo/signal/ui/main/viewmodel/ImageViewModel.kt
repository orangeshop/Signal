package com.ongo.signal.ui.main.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.BoardImagesItemDTO
import com.ongo.signal.data.repository.main.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
                MultipartBody.Part.createFormData("file", url)
            }

            val parts = newParts + existingParts
            imageRepository.uploadImage(boardId, parts)
        }.mapCatching { response ->
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    List(uris.size + existingUrls.size) { _ -> body.string() }
                } ?: emptyList<String?>()
            } else {
                val error = response.errorBody()?.string()
                Timber.e("Failed to upload images: $error")
                emptyList()
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

    private fun addImageUrlToBoard(boardId: Long, url: String) {
        _boardImages.value = _boardImages.value.toMutableMap().apply {
            this[boardId] =
                this[boardId]?.plus(BoardImagesItemDTO(boardId = boardId, fileUrl = url))
                    ?: listOf(
                        BoardImagesItemDTO(boardId = boardId, fileUrl = url)
                    )
        }
    }
}
