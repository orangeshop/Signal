package com.ongo.signal.data.repository.main.image

import com.ongo.signal.data.model.main.BoardImagesDTO
import com.ongo.signal.network.MainApi
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val mainApi: MainApi
) : ImageRepository {
    override suspend fun getBoardImages(): Response<BoardImagesDTO> {
        return mainApi.getBoardImages()
    }

    override suspend fun updateImage(
        boardId: Long,
        image: List<MultipartBody.Part>
    ): Response<ResponseBody> {
        return mainApi.updateImage(boardId, image)
    }
}
