package com.ongo.signal.ui.main

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ongo.signal.data.model.main.BoardImagesDTO
import com.ongo.signal.data.model.main.BoardImagesItemDTO
import com.ongo.signal.data.repository.main.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun loadBoardImages() {
        viewModelScope.launch {
            runCatching {
                imageRepository.getBoardImages()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val images = response.body() ?: BoardImagesDTO()
                    _boardImages.value = images.groupBy { it.boardId }
                    Timber.d("Board images loaded: ${_boardImages.value}")
                } else {
                    Timber.e("Failed to load board images: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to load board images")
            }
        }
    }

    fun uploadImage(boardId: Long, uri: Uri, context: Context) {
        viewModelScope.launch {
            runCatching {
                val file = createFileFromUri(uri, context)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                imageRepository.uploadImage(boardId, body)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.string()
                    if (imageUrl != null) {
                        addImageUrlToBoard(boardId, imageUrl)
                    }
                    Timber.d("Image uploaded: $imageUrl")
                } else {
                    Timber.e("Failed to upload image: ${response.errorBody()?.string()}")
                }
            }.onFailure { e ->
                Timber.e(e, "Failed to upload image")
            }
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
            this[boardId] = this[boardId]?.plus(BoardImagesItemDTO(boardId = boardId, fileUrl = url)) ?: listOf(BoardImagesItemDTO(boardId = boardId, fileUrl = url))
        }
    }
}
