package com.ongo.signal.data.repository.main.image

import com.ongo.signal.data.model.main.BoardImagesDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

interface ImageRepository {
    suspend fun getBoardImages(): Response<BoardImagesDTO>
    suspend fun uploadImage(boardId: Long, image: MultipartBody.Part): Response<ResponseBody>
}
