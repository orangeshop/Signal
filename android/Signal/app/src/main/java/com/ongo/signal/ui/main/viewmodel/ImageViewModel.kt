package com.ongo.signal.ui.main.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.BoardImagesDTO
import com.ongo.signal.data.model.main.BoardImagesItemDTO
import com.ongo.signal.data.repository.main.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    fun uploadImage(boardId: Long, uri: Uri, context: Context): Deferred<Result<String?>> = viewModelScope.async {
        Timber.d("uploadImage called with boardId: $boardId, uri: $uri")
        runCatching {
            val file = createFileFromUri(uri, context)
            Timber.d("File created: ${file.absolutePath}")
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            imageRepository.uploadImage(boardId, body)
        }.mapCatching { response ->
            if (response.isSuccessful) {
                val imageUrl = response.body()?.string()
                Timber.d("Image uploaded: $imageUrl")
                imageUrl
            } else {
                val error = response.errorBody()?.string()
                Timber.e("Failed to upload image: $error")
                null
            }
        }.onFailure { e ->
            Timber.e(e, "Failed to upload image")
        }
    }

    private fun createFileFromUri(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image.jpg")
        try {
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
        } catch (e: IOException) {
            Timber.e(e, "Failed to create file from URI")
        }
        return file
    }

    private fun addImageUrlToBoard(boardId: Long, url: String) {
        _boardImages.value = _boardImages.value.toMutableMap().apply {
            this[boardId] =
                this[boardId]?.plus(BoardImagesItemDTO(boardId = boardId, fileUrl = url)) ?: listOf(
                    BoardImagesItemDTO(boardId = boardId, fileUrl = url)
                )
        }
    }
}
