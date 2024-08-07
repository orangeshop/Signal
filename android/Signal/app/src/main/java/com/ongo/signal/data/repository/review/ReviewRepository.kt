package com.ongo.signal.data.repository.review

import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.data.model.review.ReviewResponseDTO
import com.ongo.signal.data.model.review.ReviewResponseItemDTO
import retrofit2.Response

interface ReviewRepository {

    suspend fun writeReview(reviewRequestDTO: ReviewRequestDTO): Response<ReviewResponseItemDTO>
    suspend fun getReview(userId: Long): Response<ReviewResponseDTO>

}