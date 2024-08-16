package com.ongo.signal.data.repository.review

import com.ongo.signal.data.model.review.ReviewRequestDTO
import com.ongo.signal.data.model.review.ReviewResponseDTO
import com.ongo.signal.data.model.review.ReviewResponseItemDTO
import com.ongo.signal.data.model.review.UserProfileResponse
import retrofit2.Response

interface ReviewRepository {

    suspend fun writeReview(reviewRequestDTO: ReviewRequestDTO): Response<ReviewResponseItemDTO>

    suspend fun getReview(userId: Long): Response<ReviewResponseDTO>

    suspend fun getUserProfile(userId: Long) : Result<UserProfileResponse?>

}