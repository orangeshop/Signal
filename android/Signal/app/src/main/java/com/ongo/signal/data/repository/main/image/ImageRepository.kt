package com.ongo.signal.data.repository.main.image

import com.ongo.signal.data.model.main.BoardImagesDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

interface ImageRepository {

    suspend fun getBoardImages(): Response<BoardImagesDTO>
    suspend fun uploadImage(boardId: Long, image: List<MultipartBody.Part>): Response<ResponseBody>
    suspend fun updateImage(boardId: Long, image: List<MultipartBody.Part>): Response<List<String>>

}
